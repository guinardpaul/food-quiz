#!/usr/bin/env python3
"""
Generates Liquibase XML data for visual carb quiz questions.
Fetches food data from Open Food Facts API and outputs 003_questions_data.xml.
"""

import json
import random
import time
import uuid
from pathlib import Path

import requests
from jinja2 import Environment, FileSystemLoader

from foods import FOODS

OFF_SEARCH_URL = "https://world.openfoodfacts.org/cgi/search.pl"
OFF_USER_AGENT = "FoodQuiz/1.0 (guinardpaul@gmail.com)"
OUTPUT_FILE = Path(__file__).parent / "003_questions_data.xml"
TEMPLATE_FILE = "template.xml.j2"

BREAD_SLICE_CARBS = 15
SUGAR_CUBE_CARBS = 4


def compute_carbs_grams(carbs_100g: float, portion_g: int) -> int:
    return round(carbs_100g * portion_g / 100)


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
    # fill remaining if needed with offset fallbacks
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


def fetch_food_data(query: str) -> dict | None:
    params = {
        "search_terms": query,
        "search_simple": 1,
        "action": "process",
        "json": 1,
        "page_size": 5,
        "fields": "product_name,image_front_url,nutriments",
    }
    try:
        resp = requests.get(OFF_SEARCH_URL, params=params, timeout=10, headers={"User-Agent": OFF_USER_AGENT})
        resp.raise_for_status()
        data = resp.json()
        products = data.get("products", [])
        for p in products:
            carbs = p.get("nutriments", {}).get("carbohydrates_100g")
            image = p.get("image_front_url")
            if carbs and image:
                return {"carbs_100g": float(carbs), "image_url": image}
    except Exception as e:
        print(f"  WARNING: fetch failed for '{query}': {e}")
    return None


def build_question(food: dict, food_data: dict) -> dict:
    carbs = compute_carbs_grams(food_data["carbs_100g"], food["portion_g"])
    distractors = generate_distractors(carbs)
    choices = [f"{v}g" for v in sorted([carbs] + distractors)]
    random.shuffle(choices)
    return {
        "label": "Combien de glucides environ ?",
        "food_name": food["name"],
        "portion_description": f"Portion standard (~{food['portion_g']}g)",
        "image_url": food_data["image_url"],
        "proposed_answers": json.dumps(choices),
        "correct_answer": f"{carbs}g",
        "equivalents": json.dumps(compute_equivalents(carbs)),
        "glycemic_impact": determine_glycemic_impact(carbs),
        "question_type": "CHOICE",
    }


def main():
    print(f"Fetching data for {len(FOODS)} foods from Open Food Facts...")
    questions = []

    for food in FOODS:
        print(f"  {food['name']}...")
        food_data = fetch_food_data(food["query"])
        if food_data:
            questions.append(build_question(food, food_data))
        else:
            print(f"  SKIPPED: no data found for {food['name']}")
        time.sleep(0.5)  # be polite to the API

    print(f"\nBuilding XML for {len(questions)} questions...")
    env = Environment(loader=FileSystemLoader(str(Path(__file__).parent)))
    template = env.get_template(TEMPLATE_FILE)
    changeset_id = f"20260524-questions-data-{uuid.uuid4().hex[:8]}"
    xml = template.render(questions=questions, changeset_id=changeset_id)

    OUTPUT_FILE.write_text(xml, encoding="utf-8")
    print(f"Done! Output written to: {OUTPUT_FILE}")
    print(f"Next step: add '003_questions_data.xml' to db.changelog-master.xml")


if __name__ == "__main__":
    main()
