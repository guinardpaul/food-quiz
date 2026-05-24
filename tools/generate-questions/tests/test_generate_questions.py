import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from generate_questions import (
    compute_carbs_grams,
    compute_equivalents,
    determine_glycemic_impact,
    generate_distractors,
)


class TestComputeCarbs:
    def test_standard_portion(self):
        assert compute_carbs_grams(28.5, 180) == 51

    def test_small_amount(self):
        assert compute_carbs_grams(10.0, 100) == 10

    def test_rounding(self):
        # 33.3 * 150 / 100 = 49.95 -> rounds to 50
        assert compute_carbs_grams(33.3, 150) == 50


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
