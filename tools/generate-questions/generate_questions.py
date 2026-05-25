#!/usr/bin/env python3
"""
Generates Liquibase XML data for visual carb quiz questions.
Uses DALL-E 3 to generate plate images and stores them as static assets.
AI-generated images are validated with Claude Vision; real photos skip validation.
"""

import argparse
import base64
import json
import os
import random
import shutil
import time
import uuid
from pathlib import Path

import anthropic
from jinja2 import Environment, FileSystemLoader
from openai import OpenAI

from foods import FOODS

SCRIPT_DIR = Path(__file__).parent
OUTPUT_FILE = SCRIPT_DIR / "003_questions_data.xml"
LIQUIBASE_FILE = SCRIPT_DIR / "../../backend/food-quiz-bootstrap/src/main/resources/db/changelog/005_replace_questions.xml"
LIQUIBASE_CHANGESET_ID = "20260524-replace-questions"
OUTPUT_IMAGES_DIR = SCRIPT_DIR / "output_images"
FRONTEND_ASSETS_DIR = SCRIPT_DIR / "../../frontend/food-quiz-app/public/assets/questions"
FRONTEND_ASSETS_REAL_DIR = FRONTEND_ASSETS_DIR / "real"
REAL_PHOTOS_MANIFEST = SCRIPT_DIR / "real_photos.json"
REAL_PHOTOS_INPUT_DIR = SCRIPT_DIR / "real_photos"
TEMPLATE_FILE = "template.xml.j2"

BREAD_SLICE_CARBS = 15
SUGAR_CUBE_CARBS = 4
MAX_VALIDATION_RETRIES = 3
VALIDATION_DELTA_PERCENT = 30


def compute_carbs_grams(carbs_100g: float, portion_g: int) -> int:
    return round(carbs_100g * portion_g / 100)


def compute_total_carbs(components: list[dict], default_portion_g: int | None = None) -> int:
    total = 0.0
    for c in components:
        portion = c.get("portion_g", default_portion_g)
        total += c["carbs_per_100g"] * portion / 100
    return round(total)


def generate_distractors(correct: int) -> list[int]:
    factors = [0.35, 0.60, 1.40, 1.70]
    random.shuffle(factors)
    distractors = set()
    for f in factors:
        candidate = round(correct * f / 5) * 5
        if candidate != correct and candidate > 0:
            distractors.add(candidate)
        if len(distractors) == 3:
            break
    offsets = [correct + 20, max(5, correct - 20), correct + 35]
    for off in offsets:
        if len(distractors) == 3:
            break
        if off != correct:
            distractors.add(off)
    return list(distractors)[:3]


def compute_equivalents(carbs_grams: int) -> dict[str, int]:
    return {
        "breadSlices": max(1, round(carbs_grams / BREAD_SLICE_CARBS)),
        "sugarCubes": max(1, round(carbs_grams / SUGAR_CUBE_CARBS)),
    }


def determine_glycemic_impact(carbs_grams: int) -> str:
    if carbs_grams < 20:
        return "LOW"
    elif carbs_grams <= 50:
        return "MEDIUM"
    return "HIGH"


def is_simple(food: dict) -> bool:
    return "portions_g" in food


def weight_to_visual_descriptor(weight_g: int, food_name_en: str) -> str:
    name = food_name_en.lower()

    if any(k in name for k in ("rice", "couscous", "bulgur", "quinoa")):
        if weight_g <= 100:
            return "a small mound covering one-quarter of the plate"
        elif weight_g <= 160:
            return "a medium mound covering one-third of the plate"
        elif weight_g <= 220:
            return "a generous mound covering half the plate"
        else:
            return "a large mound covering two-thirds of the plate"

    if any(k in name for k in ("pasta", "noodle", "spaghetti", "penne")):
        if weight_g <= 120:
            return "a small pile covering one-quarter of the plate"
        elif weight_g <= 200:
            return "a medium pile covering one-third of the plate"
        else:
            return "a generous pile covering half the plate"

    if any(k in name for k in ("fries", "french fry")):
        if weight_g <= 100:
            return "a small pile"
        elif weight_g <= 150:
            return "a medium pile covering one-quarter of the plate"
        else:
            return "a large pile covering one-third of the plate"

    if "potato" in name:
        if weight_g <= 100:
            return "2 to 3 small pieces"
        elif weight_g <= 200:
            return "4 to 5 medium pieces covering one-third of the plate"
        else:
            return "6 to 8 pieces covering half the plate"

    if any(k in name for k in ("bread", "baguette", "brioche", "wrap", "toast")):
        slices = max(1, round(weight_g / 40))
        return f"{slices} {'slice' if slices == 1 else 'slices'}"

    if any(k in name for k in ("pancake", "crepe", "waffle")):
        count = max(1, round(weight_g / 60))
        return f"{count} {'piece' if count == 1 else 'pieces'}"

    if any(k in name for k in ("oat", "muesli", "corn flake", "cereal")):
        return "a bowl filled to about two-thirds"

    if weight_g < 80:
        return "a small portion"
    elif weight_g < 150:
        return "a medium portion covering one-third of the plate"
    elif weight_g < 250:
        return "a generous portion covering half the plate"
    else:
        return "a large portion covering two-thirds of the plate"


def build_prompt(food: dict, portion_g: int | None = None) -> str:
    components = food["components"]
    if portion_g is not None:
        subject = components[0]["name_en"]
        descriptor = weight_to_visual_descriptor(portion_g, subject)
        return (
            f"Realistic food photography from a natural seated perspective, "
            f"camera at 45 degrees angle looking down at {subject} on a white ceramic "
            f"dinner plate 26cm diameter, {descriptor}, "
            "natural soft lighting, light neutral background, "
            "high detail, appetizing, no text, no labels, no logo"
        )
    else:
        names = [c["name_en"] for c in components]
        if len(names) == 1:
            subject = names[0]
        elif len(names) == 2:
            subject = f"{names[0]} with {names[1]}"
        else:
            subject = f"{names[0]} with {names[1]} and {names[2]}"
        return (
            f"Realistic food photography from a natural seated perspective, "
            f"camera at 45 degrees angle looking down at a complete meal plate: {subject}, "
            "served on a white ceramic dinner plate 26cm diameter, "
            "restaurant quality plating, natural soft lighting, "
            "light neutral background, high detail, appetizing, "
            "no text, no labels, no logo"
        )


def validate_image_portion(
    anthropic_client: anthropic.Anthropic,
    image_path: Path,
    food: dict,
    portion_g: int | None,
) -> dict:
    if portion_g is not None:
        target_g = portion_g
        food_description = food["components"][0]["name_en"]
    else:
        target_g = sum(
            c["portion_g"] for c in food["components"] if c["carbs_per_100g"] > 3
        )
        food_description = ", ".join(c["name_en"] for c in food["components"])

    lower_bound = round(target_g * (1 - VALIDATION_DELTA_PERCENT / 100))
    upper_bound = round(target_g * (1 + VALIDATION_DELTA_PERCENT / 100))

    ext = Path(image_path).suffix.lower()
    media_type = "image/jpeg" if ext in (".jpg", ".jpeg") else "image/png"

    with open(image_path, "rb") as f:
        image_data = base64.standard_b64encode(f.read()).decode("utf-8")

    prompt = (
        f"You are a dietitian validating food portion images for a diabetes training app.\n"
        f"The image should show: {food_description}\n"
        f"Target portion weight: {target_g}g (acceptable range: {lower_bound}–{upper_bound}g)\n\n"
        "Analyze the image and respond ONLY with a JSON object:\n"
        "{\n"
        '  "estimated_weight_g": <your best estimate of the depicted food weight in grams>,\n'
        '  "weight_plausible": <true if estimated weight falls within the acceptable range>,\n'
        '  "visually_clear": <true if a learner could reasonably estimate the portion from this image>,\n'
        '  "reject_reason": <null or short string explaining why to reject>,\n'
        '  "visual_cues": <one sentence describing what visual landmarks help estimate the portion>\n'
        "}"
    )

    response = anthropic_client.messages.create(
        model="claude-haiku-4-5-20251001",
        max_tokens=300,
        messages=[
            {
                "role": "user",
                "content": [
                    {
                        "type": "image",
                        "source": {
                            "type": "base64",
                            "media_type": media_type,
                            "data": image_data,
                        },
                    },
                    {"type": "text", "text": prompt},
                ],
            }
        ],
    )

    raw = response.content[0].text.strip()
    if raw.startswith("```"):
        raw = raw.split("```")[1]
        if raw.startswith("json"):
            raw = raw[4:]
    return json.loads(raw.strip())


def generate_plate_image(client: OpenAI, prompt: str, slug: str, portion_g: int | None = None) -> str:
    suffix = f"_{portion_g}g" if portion_g is not None else ""
    filename = f"{slug}{suffix}.jpg"
    output_path = OUTPUT_IMAGES_DIR / filename

    if not output_path.exists():
        response = client.images.generate(
            model="gpt-image-1",
            prompt=prompt,
            size="1024x1024",
            quality="medium",
            n=1,
        )
        img_data = base64.b64decode(response.data[0].b64_json)
        OUTPUT_IMAGES_DIR.mkdir(parents=True, exist_ok=True)
        output_path.write_bytes(img_data)
        print(f"    generated: {filename}")
    else:
        print(f"    (cached)   {filename}")

    frontend_path = FRONTEND_ASSETS_DIR / filename
    frontend_path.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(output_path, frontend_path)

    return f"/assets/questions/{filename}"


def generate_and_validate(
    openai_client: OpenAI,
    anthropic_client: anthropic.Anthropic,
    food: dict,
    slug: str,
    portion_g: int | None,
) -> str:
    suffix = f"_{portion_g}g" if portion_g is not None else ""
    cache_path = OUTPUT_IMAGES_DIR / f"{slug}{suffix}.jpg"
    frontend_path = FRONTEND_ASSETS_DIR / f"{slug}{suffix}.jpg"

    for attempt in range(1, MAX_VALIDATION_RETRIES + 1):
        prompt = build_prompt(food, portion_g)
        image_url = generate_plate_image(openai_client, prompt, slug, portion_g)
        result = validate_image_portion(anthropic_client, cache_path, food, portion_g)

        if result["weight_plausible"] and result["visually_clear"]:
            print(f"    ✓ validated (attempt {attempt}): {result['visual_cues']}")
            return image_url

        print(f"    ✗ rejected (attempt {attempt}): {result.get('reject_reason', 'unknown reason')}")
        cache_path.unlink(missing_ok=True)
        frontend_path.unlink(missing_ok=True)

    print(f"    ⚠ all {MAX_VALIDATION_RETRIES} attempts failed — image_url left empty")
    return ""


def load_real_photos_manifest() -> set[str]:
    if not REAL_PHOTOS_MANIFEST.exists():
        return set()
    with open(REAL_PHOTOS_MANIFEST, encoding="utf-8") as f:
        data = json.load(f)
    return set(data.get("slugs", []))


def copy_real_photo(slug: str) -> str:
    source = REAL_PHOTOS_INPUT_DIR / f"{slug}.jpg"
    if not source.exists():
        raise FileNotFoundError(
            f"Real photo not found: {source}\n"
            f"Place {slug}.jpg in {REAL_PHOTOS_INPUT_DIR}/"
        )
    FRONTEND_ASSETS_REAL_DIR.mkdir(parents=True, exist_ok=True)
    dest = FRONTEND_ASSETS_REAL_DIR / f"{slug}.jpg"
    shutil.copy2(source, dest)
    return f"/assets/questions/real/{slug}.jpg"


def build_question(food: dict, image_url: str, carbs: int, portion_g: int | None = None) -> dict:
    distractors = generate_distractors(carbs)
    choices = [f"{v}g" for v in sorted([carbs] + distractors)]
    random.shuffle(choices)

    if portion_g is not None:
        portion_desc = f"Portion standard (~{portion_g}g)"
    else:
        total_g = sum(c["portion_g"] for c in food["components"])
        portion_desc = f"Assiette complète (~{total_g}g)"

    return {
        "label": "Combien de glucides environ ?",
        "food_name": food["name"],
        "portion_description": portion_desc,
        "image_url": image_url,
        "proposed_answers": json.dumps(choices),
        "correct_answer": f"{carbs}g",
        "equivalents": json.dumps(compute_equivalents(carbs)),
        "glycemic_impact": determine_glycemic_impact(carbs),
        "question_type": "CHOICE",
    }


def main():
    parser = argparse.ArgumentParser(description="Generate quiz questions with gpt-image-1 plate images")
    parser.add_argument("--limit", type=int, default=None, help="Limit to N food entries (for testing/prompt tuning)")
    parser.add_argument("--no-images", action="store_true", help="Skip image generation, set image_url to empty string (no API keys required)")
    parser.add_argument("--with-delete", action="store_true", help="Output to 005_replace_questions.xml with DELETE FROM questions (for Liquibase migration)")
    args = parser.parse_args()

    openai_client = None
    anthropic_client = None

    if not args.no_images:
        api_key = os.environ.get("OPENAI_API_KEY")
        if not api_key:
            raise SystemExit("ERROR: OPENAI_API_KEY environment variable not set")
        openai_client = OpenAI(api_key=api_key)

        anthropic_key = os.environ.get("ANTHROPIC_API_KEY")
        if not anthropic_key:
            raise SystemExit("ERROR: ANTHROPIC_API_KEY environment variable not set")
        anthropic_client = anthropic.Anthropic(api_key=anthropic_key)

    real_slugs = load_real_photos_manifest()
    if real_slugs:
        print(f"Real photos manifest: {len(real_slugs)} slug(s) will use real photos")

    foods = FOODS[: args.limit] if args.limit is not None else FOODS
    print(f"{'Generating questions (no images)' if args.no_images else 'Generating images'} for {len(foods)} food entries...")

    questions = []

    for food in foods:
        if is_simple(food):
            for portion_g in food["portions_g"]:
                print(f"  {food['name']} ({portion_g}g)...")
                carbs = compute_total_carbs(food["components"], portion_g)
                if args.no_images:
                    image_url = ""
                elif food["slug"] in real_slugs:
                    image_url = copy_real_photo(food["slug"])
                    print(f"    real photo: {image_url}")
                else:
                    image_url = generate_and_validate(openai_client, anthropic_client, food, food["slug"], portion_g)
                    time.sleep(0.5)
                questions.append(build_question(food, image_url, carbs, portion_g))
        else:
            print(f"  {food['name']} (plat composé)...")
            carbs = compute_total_carbs(food["components"])
            if args.no_images:
                image_url = ""
            elif food["slug"] in real_slugs:
                image_url = copy_real_photo(food["slug"])
                print(f"    real photo: {image_url}")
            else:
                image_url = generate_and_validate(openai_client, anthropic_client, food, food["slug"], None)
                time.sleep(0.5)
            questions.append(build_question(food, image_url, carbs))

    print(f"\nBuilding XML for {len(questions)} questions...")
    env = Environment(loader=FileSystemLoader(str(SCRIPT_DIR)))
    template = env.get_template(TEMPLATE_FILE)

    if args.with_delete:
        xml = template.render(questions=questions, changeset_id=LIQUIBASE_CHANGESET_ID, include_delete=True)
        LIQUIBASE_FILE.write_text(xml, encoding="utf-8")
        print(f"Done! Liquibase changeset written to: {LIQUIBASE_FILE.resolve()}")
        print("Next step: commit the images and 005_replace_questions.xml, then merge to main")
    else:
        changeset_id = f"20260524-questions-data-{uuid.uuid4().hex[:8]}"
        xml = template.render(questions=questions, changeset_id=changeset_id, include_delete=False)
        OUTPUT_FILE.write_text(xml, encoding="utf-8")
        print(f"Done! Output written to: {OUTPUT_FILE}")


if __name__ == "__main__":
    main()
