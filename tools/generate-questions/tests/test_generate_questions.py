import json
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from generate_questions import (
    build_prompt,
    compute_carbs_grams,
    compute_equivalents,
    compute_total_carbs,
    copy_real_photo,
    determine_glycemic_impact,
    generate_distractors,
    generate_plate_image,
    is_simple,
    load_real_photos_manifest,
    validate_image_portion,
    weight_to_visual_descriptor,
)

SIMPLE_FOOD = {
    "name": "Riz blanc cuit",
    "slug": "riz-blanc-cuit",
    "portions_g": [180],
    "components": [
        {"name_en": "cooked white rice", "carbs_per_100g": 27.9},
    ],
}

COMPLEX_FOOD = {
    "name": "Assiette riz et poulet grillé",
    "slug": "assiette-riz-poulet-grille",
    "components": [
        {"name_en": "cooked white rice", "portion_g": 150, "carbs_per_100g": 27.9},
        {"name_en": "grilled chicken breast", "portion_g": 120, "carbs_per_100g": 0.0},
    ],
}

THREE_COMPONENT_FOOD = {
    "name": "Assiette riz lentilles carottes",
    "slug": "assiette-riz-lentilles-carottes",
    "components": [
        {"name_en": "cooked white rice", "portion_g": 120, "carbs_per_100g": 27.9},
        {"name_en": "cooked green lentils", "portion_g": 100, "carbs_per_100g": 16.8},
        {"name_en": "cooked carrots", "portion_g": 80, "carbs_per_100g": 6.5},
    ],
}


class TestComputeCarbs:
    def test_standard_portion(self):
        assert compute_carbs_grams(28.5, 180) == 51

    def test_small_amount(self):
        assert compute_carbs_grams(10.0, 100) == 10

    def test_rounding(self):
        # 33.3 * 150 / 100 = 49.95 -> rounds to 50
        assert compute_carbs_grams(33.3, 150) == 50


class TestComputeTotalCarbs:
    def test_simple_food(self):
        # 27.9 * 180 / 100 = 50.22 → 50
        assert compute_total_carbs(SIMPLE_FOOD["components"], 180) == 50

    def test_complex_dish_sums_components(self):
        # rice: 27.9 * 150 / 100 = 41.85, chicken: 0 → total = 42
        assert compute_total_carbs(COMPLEX_FOOD["components"]) == 42

    def test_complex_dish_with_multiple_carb_components(self):
        components = [
            {"name_en": "cooked white rice", "portion_g": 120, "carbs_per_100g": 27.9},
            {"name_en": "cooked green lentils", "portion_g": 100, "carbs_per_100g": 16.8},
            {"name_en": "cooked carrots", "portion_g": 80, "carbs_per_100g": 6.5},
        ]
        # rice: 33.48, lentils: 16.8, carrots: 5.2 → total 55.48 → 55
        assert compute_total_carbs(components) == 55


class TestIsSimple:
    def test_simple_food_has_portions_g(self):
        assert is_simple(SIMPLE_FOOD) is True

    def test_complex_dish_has_no_portions_g(self):
        assert is_simple(COMPLEX_FOOD) is False


class TestWeightToVisualDescriptor:
    def test_rice_small(self):
        result = weight_to_visual_descriptor(80, "cooked white rice")
        assert "one-quarter" in result

    def test_rice_medium(self):
        result = weight_to_visual_descriptor(150, "cooked white rice")
        assert "one-third" in result

    def test_rice_generous(self):
        result = weight_to_visual_descriptor(200, "cooked white rice")
        assert "half" in result

    def test_bread_returns_slice_count(self):
        result = weight_to_visual_descriptor(40, "French baguette bread")
        assert "slice" in result

    def test_pasta_medium(self):
        result = weight_to_visual_descriptor(200, "cooked pasta")
        assert "one-third" in result

    def test_generic_fallback_small(self):
        result = weight_to_visual_descriptor(50, "unknown food item")
        assert "small" in result

    def test_generic_fallback_generous(self):
        result = weight_to_visual_descriptor(200, "unknown food item")
        assert "half" in result


class TestBuildPrompt:
    def test_simple_food_includes_ingredient(self):
        prompt = build_prompt(SIMPLE_FOOD, 180)
        assert "cooked white rice" in prompt

    def test_simple_food_uses_45_degree_angle(self):
        prompt = build_prompt(SIMPLE_FOOD, 180)
        assert "45 degrees" in prompt

    def test_simple_food_includes_photography_keywords(self):
        prompt = build_prompt(SIMPLE_FOOD, 180)
        assert "food photography" in prompt.lower()
        assert "no text" in prompt.lower()

    def test_simple_food_does_not_include_grams_in_prompt(self):
        prompt = build_prompt(SIMPLE_FOOD, 180)
        assert "180g" not in prompt

    def test_simple_food_different_portions_produce_different_prompts(self):
        prompt_100 = build_prompt(SIMPLE_FOOD, 100)
        prompt_200 = build_prompt(SIMPLE_FOOD, 200)
        assert prompt_100 != prompt_200

    def test_complex_dish_includes_all_components(self):
        prompt = build_prompt(COMPLEX_FOOD)
        assert "cooked white rice" in prompt
        assert "grilled chicken breast" in prompt

    def test_complex_dish_includes_complete_meal_plate(self):
        prompt = build_prompt(COMPLEX_FOOD)
        assert "complete meal plate" in prompt

    def test_complex_dish_uses_45_degree_angle(self):
        prompt = build_prompt(COMPLEX_FOOD)
        assert "45 degrees" in prompt

    def test_three_component_dish_uses_and_separator(self):
        prompt = build_prompt(THREE_COMPONENT_FOOD)
        assert " and " in prompt

    def test_two_component_dish_uses_with_separator(self):
        prompt = build_prompt(COMPLEX_FOOD)
        assert " with " in prompt

    def test_complex_dish_does_not_include_grams_in_prompt(self):
        prompt = build_prompt(COMPLEX_FOOD)
        assert "150g" not in prompt
        assert "120g" not in prompt


class TestDetermineGlycemicImpact:
    def test_low(self):
        assert determine_glycemic_impact(15) == "LOW"

    def test_low_boundary(self):
        assert determine_glycemic_impact(19) == "LOW"

    def test_medium(self):
        assert determine_glycemic_impact(20) == "MEDIUM"
        assert determine_glycemic_impact(35) == "MEDIUM"
        assert determine_glycemic_impact(50) == "MEDIUM"

    def test_high(self):
        assert determine_glycemic_impact(51) == "HIGH"
        assert determine_glycemic_impact(80) == "HIGH"


class TestGenerateDistractors:
    def test_returns_three_values(self):
        result = generate_distractors(50)
        assert len(result) == 3

    def test_does_not_include_correct_answer(self):
        for _ in range(20):
            result = generate_distractors(50)
            assert 50 not in result

    def test_all_positive(self):
        for correct in [5, 10, 20, 50, 100]:
            result = generate_distractors(correct)
            assert all(v > 0 for v in result)


class TestComputeEquivalents:
    def test_bread_slices(self):
        # 60g / 15 = 4 slices
        eq = compute_equivalents(60)
        assert eq["breadSlices"] == 4

    def test_sugar_cubes(self):
        # 60g / 4 = 15 cubes
        eq = compute_equivalents(60)
        assert eq["sugarCubes"] == 15

    def test_minimum_one(self):
        eq = compute_equivalents(2)
        assert eq["breadSlices"] >= 1
        assert eq["sugarCubes"] >= 1


import base64 as _base64

_FAKE_B64 = _base64.b64encode(b"fakeimage").decode()


def _make_mock_openai_client(api_called: list | None = None):
    class _MockImages:
        def generate(self, **kwargs):
            if api_called is not None:
                api_called.append(True)

            class _Item:
                b64_json = _FAKE_B64

            class _Resp:
                data = [_Item()]

            return _Resp()

    class _MockClient:
        images = _MockImages()

    return _MockClient()


def _make_mock_anthropic_client(response_payload: dict):
    raw = json.dumps(response_payload)

    class _MockContent:
        text = raw

    class _MockResponse:
        content = [_MockContent()]

    class _MockMessages:
        def create(self, **kwargs):
            return _MockResponse()

    class _MockClient:
        messages = _MockMessages()

    return _MockClient()


class TestGeneratePlateImage:
    def test_simple_image_path_includes_portion(self, tmp_path, monkeypatch):
        import generate_questions

        monkeypatch.setattr(generate_questions, "OUTPUT_IMAGES_DIR", tmp_path / "output")
        monkeypatch.setattr(generate_questions, "FRONTEND_ASSETS_DIR", tmp_path / "frontend")

        result = generate_plate_image(_make_mock_openai_client(), "test prompt", "riz-blanc-cuit", 180)

        assert result == "/assets/questions/riz-blanc-cuit_180g.jpg"
        assert (tmp_path / "output" / "riz-blanc-cuit_180g.jpg").exists()
        assert (tmp_path / "frontend" / "riz-blanc-cuit_180g.jpg").exists()

    def test_complex_image_path_has_no_portion_suffix(self, tmp_path, monkeypatch):
        import generate_questions

        monkeypatch.setattr(generate_questions, "OUTPUT_IMAGES_DIR", tmp_path / "output")
        monkeypatch.setattr(generate_questions, "FRONTEND_ASSETS_DIR", tmp_path / "frontend")

        result = generate_plate_image(_make_mock_openai_client(), "test prompt", "assiette-riz-poulet")

        assert result == "/assets/questions/assiette-riz-poulet.jpg"

    def test_cached_image_is_not_regenerated(self, tmp_path, monkeypatch):
        import generate_questions

        output_dir = tmp_path / "output"
        output_dir.mkdir()
        frontend_dir = tmp_path / "frontend"
        frontend_dir.mkdir()
        monkeypatch.setattr(generate_questions, "OUTPUT_IMAGES_DIR", output_dir)
        monkeypatch.setattr(generate_questions, "FRONTEND_ASSETS_DIR", frontend_dir)

        (output_dir / "riz-blanc-cuit_180g.jpg").write_bytes(b"cached")

        api_called = []
        generate_plate_image(_make_mock_openai_client(api_called), "test prompt", "riz-blanc-cuit", 180)

        assert not api_called, "API should not be called when image is already cached"


class TestValidateImagePortion:
    def test_returns_plausible_result(self, tmp_path):
        image_path = tmp_path / "test.jpg"
        image_path.write_bytes(b"fakeimage")

        payload = {
            "estimated_weight_g": 175,
            "weight_plausible": True,
            "visually_clear": True,
            "reject_reason": None,
            "visual_cues": "The rice mound covers about one-third of the plate",
        }
        client = _make_mock_anthropic_client(payload)
        result = validate_image_portion(client, image_path, SIMPLE_FOOD, 180)

        assert result["weight_plausible"] is True
        assert result["visually_clear"] is True
        assert result["estimated_weight_g"] == 175

    def test_returns_rejected_result(self, tmp_path):
        image_path = tmp_path / "test.jpg"
        image_path.write_bytes(b"fakeimage")

        payload = {
            "estimated_weight_g": 350,
            "weight_plausible": False,
            "visually_clear": True,
            "reject_reason": "Portion appears much larger than target",
            "visual_cues": "The rice covers most of the plate",
        }
        client = _make_mock_anthropic_client(payload)
        result = validate_image_portion(client, image_path, SIMPLE_FOOD, 180)

        assert result["weight_plausible"] is False
        assert result["reject_reason"] == "Portion appears much larger than target"

    def test_strips_markdown_code_fences(self, tmp_path):
        image_path = tmp_path / "test.jpg"
        image_path.write_bytes(b"fakeimage")

        payload = {
            "estimated_weight_g": 160,
            "weight_plausible": True,
            "visually_clear": True,
            "reject_reason": None,
            "visual_cues": "Looks good",
        }

        class _FencedContent:
            text = "```json\n" + json.dumps(payload) + "\n```"

        class _FencedResponse:
            content = [_FencedContent()]

        class _FencedMessages:
            def create(self, **kwargs):
                return _FencedResponse()

        class _FencedClient:
            messages = _FencedMessages()

        result = validate_image_portion(_FencedClient(), image_path, SIMPLE_FOOD, 180)
        assert result["estimated_weight_g"] == 160

    def test_complex_food_target_excludes_zero_carb_components(self, tmp_path):
        image_path = tmp_path / "test.jpg"
        image_path.write_bytes(b"fakeimage")

        api_args = {}

        class _CapturingMessages:
            def create(self, **kwargs):
                api_args.update(kwargs)

                class _Content:
                    text = json.dumps({
                        "estimated_weight_g": 150,
                        "weight_plausible": True,
                        "visually_clear": True,
                        "reject_reason": None,
                        "visual_cues": "ok",
                    })

                class _Resp:
                    content = [_Content()]

                return _Resp()

        class _CapturingClient:
            messages = _CapturingMessages()

        validate_image_portion(_CapturingClient(), image_path, COMPLEX_FOOD, None)
        prompt_text = api_args["messages"][0]["content"][1]["text"]
        # target_g for COMPLEX_FOOD = only rice (carbs > 3): 150g; chicken excluded
        assert "150g" in prompt_text


class TestLoadRealPhotosManifest:
    def test_missing_file_returns_empty_set(self, tmp_path, monkeypatch):
        import generate_questions

        monkeypatch.setattr(generate_questions, "REAL_PHOTOS_MANIFEST", tmp_path / "nonexistent.json")
        result = load_real_photos_manifest()
        assert result == set()

    def test_valid_manifest_returns_slugs(self, tmp_path, monkeypatch):
        import generate_questions

        manifest = tmp_path / "real_photos.json"
        manifest.write_text(json.dumps({"slugs": ["riz-blanc-cuit", "pates-cuites"]}))
        monkeypatch.setattr(generate_questions, "REAL_PHOTOS_MANIFEST", manifest)
        result = load_real_photos_manifest()
        assert result == {"riz-blanc-cuit", "pates-cuites"}

    def test_empty_slugs_list(self, tmp_path, monkeypatch):
        import generate_questions

        manifest = tmp_path / "real_photos.json"
        manifest.write_text(json.dumps({"slugs": []}))
        monkeypatch.setattr(generate_questions, "REAL_PHOTOS_MANIFEST", manifest)
        result = load_real_photos_manifest()
        assert result == set()


class TestCopyRealPhoto:
    def test_copies_photo_and_returns_url(self, tmp_path, monkeypatch):
        import generate_questions

        input_dir = tmp_path / "real_photos"
        input_dir.mkdir()
        (input_dir / "riz-blanc-cuit.jpg").write_bytes(b"realphoto")
        frontend_real_dir = tmp_path / "frontend" / "real"

        monkeypatch.setattr(generate_questions, "REAL_PHOTOS_INPUT_DIR", input_dir)
        monkeypatch.setattr(generate_questions, "FRONTEND_ASSETS_REAL_DIR", frontend_real_dir)

        result = copy_real_photo("riz-blanc-cuit")

        assert result == "/assets/questions/real/riz-blanc-cuit.jpg"
        assert (frontend_real_dir / "riz-blanc-cuit.jpg").read_bytes() == b"realphoto"

    def test_raises_when_source_missing(self, tmp_path, monkeypatch):
        import generate_questions
        import pytest

        input_dir = tmp_path / "real_photos"
        input_dir.mkdir()
        monkeypatch.setattr(generate_questions, "REAL_PHOTOS_INPUT_DIR", input_dir)
        monkeypatch.setattr(generate_questions, "FRONTEND_ASSETS_REAL_DIR", tmp_path / "frontend" / "real")

        with pytest.raises(FileNotFoundError):
            copy_real_photo("nonexistent-slug")
