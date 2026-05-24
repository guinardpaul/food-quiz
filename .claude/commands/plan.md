---
description: Planifier et implémenter une fonctionnalité depuis main
argument-hint: <description de la fonctionnalité>
allowed-tools: Bash(git:*), Bash(gh:*)
---

Tu vas planifier et implémenter la fonctionnalité suivante : $ARGUMENTS

## Étape 1 — Questions de clarification

Avant toute chose, pose des questions sur les points ambigus. Pour chaque
incertitude, propose aussi une alternative ou une meilleure approche si tu
en vois une. Attends une réponse avant de continuer.

Exemples de points à clarifier :
- Périmètre exact de la fonctionnalité
- Contraintes techniques ou de compatibilité
- Dépendances existantes dans le code
- Préférences sur l'approche (ex : plusieurs solutions possibles → lesquelles ?)

## Étape 2 — Présentation du plan

Présente un plan structuré avec :
- Les fichiers à créer ou modifier
- L'approche technique retenue (et pourquoi)
- Les étapes d'implémentation dans l'ordre
- Les risques ou points d'attention

Demande une validation explicite avant de continuer.

## Étape 3 — Proposition d'issues GitHub

Propose de créer une ou plusieurs issues GitHub liées à ce plan.
Si le plan couvre plusieurs périmètres distincts, propose de les
découper en issues séparées avec des titres clairs.

Pour chaque issue proposée, indique :
- Le titre
- Une description courte
- Les labels suggérés (enhancement, bug, etc.)

Demande confirmation avant de créer les issues avec `gh issue create`.

## Étape 4 — Mise en place de la branche

Seulement après validation du plan et des issues :

```bash
git checkout main
git pull origin main
git checkout -b feat/<nom-court-descriptif>
```

Annonce la branche créée.

## Étape 5 — Implémentation

Implémente le plan validé. Signale chaque étape terminée avant de passer
à la suivante.