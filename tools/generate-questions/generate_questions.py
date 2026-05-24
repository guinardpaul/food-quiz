#!/usr/bin/env python3
"""
Generates Liquibase XML data for visual carb quiz questions.
Uses DALL-E 3 to generate plate images and stores them as static assets.
"""

import argparse
import json
import os
import random
import shutil
import time
import uuid
from pathlib import Path

from jinja2 import Environment, FileSystemLoader
from openai import OpenAI

from foods import FOODS

SCRIPT_DIR = Path(__file__).parent
OUTPUT_FILE = SCRIPT_DIR / "003_questions_data.xml"
OUTPUT_IMAGES_DIR = SCRIPT_DIR / "output_images"
FRONTEND_ASSETS_DIR = SCRIPT_DIR / "../../frontend/food-quiz-app/public/assets/questions"
TEMPLATE_FILE = "template.xml.j2"

BREAD_SLICE_CARBS = 15
SUGAR_CUBE_CARBS = 4


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


def build_prompt(food: dict, portion_g: int | None = None) -> str:
    components = food["components"]
    if portion_g is not None:
        subject = components[0]["name_en"]
        return (
            f"Realistic overhead food photography of {subject} on a white ceramic plate, "
            "generous serving, natural soft lighting, light neutral background, "
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
            f"Realistic overhead food photography of a complete meal plate: {subject}, "
            "served on a white ceramic plate, restaurant quality plating, "
            "natural soft lighting, light neutral background, high detail, appetizing, "
            "no text, no labels, no logo"
        )


def generate_plate_image(client: OpenAI, prompt: str, slug: str, portion_g: int | None = None) -> str:
    suffix = f"_{portion_g}g" if portion_g is not None else ""
    filename = f"{slug}{suffix}.jpg"
    output_path = OUTPUT_IMAGES_DIR / filename

    if not output_path.exists():
        import base64
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
    parser = argparse.ArgumentParser(description="Generate quiz questions with DALL-E 3 plate images")
    parser.add_argument("--limit", type=int, default=None, help="Limit to N food entries (for testing/prompt tuning)")
    parser.add_argument("--no-images", action="store_true", help="Skip DALL-E generation, set image_url to empty string (no OPENAI_API_KEY required)")
    args = parser.parse_args()

    client = None
    if not args.no_images:
        api_key = os.environ.get("OPENAI_API_KEY")
        if not api_key:
            raise SystemExit("ERROR: OPENAI_API_KEY environment variable not set")
        client = OpenAI(api_key=api_key)

    foods = FOODS[: args.limit] if args.limit is not None else FOODS

    print(f"{'Generating questions (no images)' if args.no_images else 'Generating images'} for {len(foods)} food entries...")
    questions = []

    for food in foods:
        if is_simple(food):
            for portion_g in food["portions_g"]:
                print(f"  {food['name']} ({portion_g}g)...")
                prompt = build_prompt(food, portion_g)
                carbs = compute_total_carbs(food["components"], portion_g)
                if args.no_images:
                    image_url = ""
                else:
                    image_url = generate_plate_image(client, prompt, food["slug"], portion_g)
                    time.sleep(0.5)
                questions.append(build_question(food, image_url, carbs, portion_g))
        else:
            print(f"  {food['name']} (plat composé)...")
            prompt = build_prompt(food)
            carbs = compute_total_carbs(food["components"])
            if args.no_images:
                image_url = ""
            else:
                image_url = generate_plate_image(client, prompt, food["slug"])
                time.sleep(0.5)
            questions.append(build_question(food, image_url, carbs))

    print(f"\nBuilding XML for {len(questions)} questions...")
    env = Environment(loader=FileSystemLoader(str(SCRIPT_DIR)))
    template = env.get_template(TEMPLATE_FILE)
    changeset_id = f"20260524-questions-data-{uuid.uuid4().hex[:8]}"
    xml = template.render(questions=questions, changeset_id=changeset_id)

    OUTPUT_FILE.write_text(xml, encoding="utf-8")
    print(f"Done! Output written to: {OUTPUT_FILE}")
    print(f"Next step: add '003_questions_data.xml' to db.changelog-master.xml")


if __name__ == "__main__":
    main()
