#!/usr/bin/env python3
"""Generate seed data from real dish photos exposed by an external API.

The initializer uses TheMealDB IDs as the source of real plate photos. The
manifest contains nutrition-estimation metadata because TheMealDB provides real
images and recipes but not reliable plated weights. Estimated carbohydrate grams
are computed from each food item's approximate plated weight and carbohydrate
ratio per 100 g.
"""

from __future__ import annotations

import argparse
import json
from html import escape
from pathlib import Path
import textwrap
import urllib.request
import uuid

DEFAULT_MANIFEST = Path(__file__).resolve().parents[1] / "data/real_plate_sources.json"
DEFAULT_OUTPUT = Path(__file__).resolve().parents[2] / "food-quiz-bootstrap/src/main/resources/db/changelog/002_seed_real_food_quizzes.xml"


class SeedGenerationError(RuntimeError):
    pass


def xml_escape(value: str) -> str:
    return escape(value, quote=True)


def carbs_g(item: dict) -> float:
    return round(item["estimated_weight_g"] * item["carbs_per_100g"] / 100, 1)


def total_carbs_g(plate: dict) -> float:
    return round(sum(carbs_g(item) for item in plate["food_items"]), 1)


def resolve_themealdb_image(plate: dict, provider_api: str, timeout_seconds: int) -> str:
    api_url = provider_api.format(meal_id=plate["provider_meal_id"])
    with urllib.request.urlopen(api_url, timeout=timeout_seconds) as response:
        payload = json.load(response)
    meals = payload.get("meals") or []
    if not meals:
        raise SeedGenerationError(f"TheMealDB returned no meal for id {plate['provider_meal_id']}")
    return meals[0].get("strMealThumb") or plate["fallback_image_url"]


def image_url_for(plate: dict, provider_api: str, timeout_seconds: int, offline: bool) -> str:
    if offline:
        return plate["fallback_image_url"]
    return resolve_themealdb_image(plate, provider_api, timeout_seconds)


def question_insert(question_id: int, label: str, image_url: str, quiz_id: str, expected_value: float, tolerance: float) -> str:
    return textwrap.dedent(f"""\
        <insert tableName="questions">
            <column name="id" valueNumeric="{question_id}" />
            <column name="label" value="{xml_escape(label)}" />
            <column name="image_url" value="{xml_escape(image_url)}" />
            <column name="expected_value" valueNumeric="{expected_value}" />
            <column name="tolerance" valueNumeric="{tolerance}" />
            <column name="quiz_id" value="{quiz_id}" />
            <column name="question_type" value="NUMBER" />
        </insert>""")


def food_item_insert(food_item_id: int, quiz_id: str, item: dict) -> str:
    return textwrap.dedent(f"""\
        <insert tableName="plate_food_items">
            <column name="id" valueNumeric="{food_item_id}" />
            <column name="quiz_id" value="{quiz_id}" />
            <column name="name" value="{xml_escape(item['name'])}" />
            <column name="estimated_weight_g" valueNumeric="{item['estimated_weight_g']}" />
            <column name="estimated_carbs_g" valueNumeric="{carbs_g(item)}" />
            <column name="carbohydrate_source" valueBoolean="{str(item['carbohydrate_source']).lower()}" />
        </insert>""")


def plate_question_inserts(base_question_id: int, plate: dict, image_url: str, quiz_id: str) -> list[str]:
    inserts = []
    question_id = base_question_id
    for item in plate["food_items"]:
        tolerance = max(10, round(item["estimated_weight_g"] * 0.15))
        label = f"D'après la photo réelle, estime le poids de {item['name']} dans l'assiette (en grammes)."
        inserts.append(question_insert(question_id, label, image_url, quiz_id, item["estimated_weight_g"], tolerance))
        question_id += 1

    inserts.append(
        question_insert(
            question_id,
            "D'après la photo réelle, estime la quantité totale de glucides de l'assiette (en grammes).",
            image_url,
            quiz_id,
            total_carbs_g(plate),
            max(5, round(total_carbs_g(plate) * 0.15)),
        )
    )
    return inserts


def build_changelog(manifest: dict, offline: bool, timeout_seconds: int) -> str:
    namespace = uuid.UUID(manifest["namespace_uuid"])
    provider_api = manifest["provider_api"]
    quiz_inserts = []
    question_inserts = []
    food_item_inserts = []

    for plate_index, plate in enumerate(manifest["plates"], start=1):
        quiz_id = str(uuid.uuid5(namespace, plate["slug"]))
        image_url = image_url_for(plate, provider_api, timeout_seconds, offline)
        quiz_inserts.append(textwrap.dedent(f"""\
            <insert tableName="quiz">
                <column name="id" valueNumeric="{plate_index}" />
                <column name="quiz_id" value="{quiz_id}" />
            </insert>"""))
        question_inserts.extend(plate_question_inserts(plate_index * 100, plate, image_url, quiz_id))
        for item_index, item in enumerate(plate["food_items"], start=1):
            food_item_inserts.append(food_item_insert(plate_index * 100 + item_index, quiz_id, item))

    changelog = textwrap.dedent(f"""\
        <databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">

            <!-- Generated by food-quiz-data-initializer/scripts/generate_seed.py from real plate photo sources. -->
            <changeSet id="20260518-seed-real-food-quizzes" author="codex">
        {chr(10).join(quiz_inserts)}

        {chr(10).join(food_item_inserts)}

        {chr(10).join(question_inserts)}
            </changeSet>
        </databaseChangeLog>
        """)
    return changelog.lstrip()


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Generate Food Quiz seed data from real plate photo sources.")
    parser.add_argument("--manifest", type=Path, default=DEFAULT_MANIFEST)
    parser.add_argument("--output", type=Path, default=DEFAULT_OUTPUT)
    parser.add_argument("--timeout-seconds", type=int, default=20)
    parser.add_argument("--offline", action="store_true", help="Use fallback real-image URLs from the manifest instead of calling the API.")
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    manifest = json.loads(args.manifest.read_text(encoding="utf-8"))
    changelog = build_changelog(manifest, args.offline, args.timeout_seconds)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(changelog, encoding="utf-8")


if __name__ == "__main__":
    main()
