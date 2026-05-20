# Food Quiz data initializer

Ce projet génère les données d'initialisation des quiz à partir de **vraies photos de plats**.

## Source des images

Le manifeste `data/real_plate_sources.json` référence des plats TheMealDB par identifiant (`provider_meal_id`). Le script `scripts/generate_seed.py` peut appeler l'API publique TheMealDB pour résoudre l'URL de la photo réelle (`strMealThumb`).

Chaque entrée contient aussi une `fallback_image_url` réelle afin de pouvoir régénérer le changelog dans un environnement sans accès réseau avec `--offline`.

## Estimations nutritionnelles

TheMealDB fournit des recettes et des photos, mais pas le poids exact des aliments dans l'assiette. Les poids par aliment sont donc stockés dans le manifeste comme estimations de portion visibles. Le script calcule ensuite automatiquement les glucides par aliment avec :

`estimated_weight_g * carbs_per_100g / 100`

Le changelog généré contient :

- une ligne `quiz` par photo de plat ;
- une ligne `plate_food_items` par aliment avec poids estimé, glucides estimés et indicateur de source glucidique ;
- une question numérique par aliment pour estimer son poids ;
- une question numérique finale pour estimer les glucides totaux de l'assiette.

## Génération

Avec accès à l'API TheMealDB :

```bash
python3 food-quiz-data-initializer/scripts/generate_seed.py
```

Sans accès réseau, en utilisant les URLs réelles de secours du manifeste :

```bash
python3 food-quiz-data-initializer/scripts/generate_seed.py --offline
```

La sortie est écrite dans `food-quiz-bootstrap/src/main/resources/db/changelog/002_seed_real_food_quizzes.xml`.
