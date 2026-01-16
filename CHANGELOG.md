# Changelog

Tous les changements notables de ce projet seront documentés dans ce fichier.

Le format est basé sur [Keep a Changelog](https://keepachangelog.com/fr/1.0.0/),
et ce projet adhère à [Semantic Versioning](https://semver.org/lang/fr/).

## [Non publié]

### Ajouté
- Squelette complet de l'application
- Architecture MVVM avec Jetpack Compose
- Base de données Room pour les alarmes
- DataStore pour les préférences
- Écran liste des alarmes
- Écran d'édition d'alarme
- Écran de paramètres
- Navigation avec Navigation Compose
- Support Material Design 3
- Thème clair/sombre
- Structure des services (AlarmScheduler, AlarmReceiver, AlarmService)
- Documentation complète (ARCHITECTURE.md, DEVELOPMENT.md, TODO.md, etc.)
- Permissions Android configurées
- Gestion des jours de répétition
- Choix de la méthode de détection (UI)
- Configuration du mode Snooze (UI)

### En cours de développement
- Time Picker Material3
- Planification réelle des alarmes avec AlarmManager
- Sonnerie et vibration
- Détection par capteurs
- Sélecteur de sonnerie
- Notifications
- Tests unitaires

## [0.1.0] - 2026-01-16

### Ajouté
- Version initiale du squelette SmartAlarm
- Structure du projet avec architecture moderne
- Documentation complète pour les développeurs
- Configuration Gradle avec Kotlin DSL
- Dépendances modernes (Compose, Room, Navigation, etc.)

---

## Format

Types de changements :
- `Ajouté` pour les nouvelles fonctionnalités.
- `Modifié` pour les changements aux fonctionnalités existantes.
- `Déprécié` pour les fonctionnalités bientôt supprimées.
- `Supprimé` pour les fonctionnalités supprimées.
- `Corrigé` pour les corrections de bugs.
- `Sécurité` en cas de vulnérabilités.

## Liens

[Non publié]: https://github.com/VOTRE_USERNAME/SmartAlarm/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/VOTRE_USERNAME/SmartAlarm/releases/tag/v0.1.0
