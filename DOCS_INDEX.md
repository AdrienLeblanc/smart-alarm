# Index de la documentation SmartAlarm

Bienvenue dans la documentation de SmartAlarm ! Ce fichier vous guide vers les différentes ressources disponibles.

## 🚀 Pour bien démarrer

Si c'est la première fois que vous travaillez sur ce projet, commencez par ici :

1. **[README.md](README.md)** - Vue d'ensemble du projet
2. **[QUICK_START.md](QUICK_START.md)** - Guide de démarrage rapide (5 minutes)
3. **[SKELETON_README.md](SKELETON_README.md)** - Description du squelette

## 📚 Documentation technique

### Architecture et conception
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Architecture détaillée du projet
  - Structure des packages
  - Patterns utilisés (MVVM, Repository)
  - Technologies et dépendances
  - Flux de données

### Développement
- **[DEVELOPMENT.md](DEVELOPMENT.md)** - Guide de développement
  - Comment ajouter une alarme
  - Comment implémenter un nouveau capteur
  - Comment ajouter un paramètre
  - Debugging et tests
  - Migration de base de données

### Tâches et planification
- **[TODO.md](TODO.md)** - Liste complète des tâches
  - Tâches critiques (MVP)
  - Tâches importantes
  - Améliorations
  - Priorisation suggérée

### Résumé
- **[SUMMARY.md](SUMMARY.md)** - Résumé complet du squelette
  - Ce qui a été créé
  - Statistiques
  - Technologies
  - Prochaines étapes

## 🤝 Contribution

- **[CONTRIBUTING.md](CONTRIBUTING.md)** - Guide de contribution
  - Comment contribuer
  - Conventions de code
  - Process de PR
  - Standards de qualité

- **[CONTRIBUTORS.md](CONTRIBUTORS.md)** - Liste des contributeurs

## 📋 Autres documents

- **[CHANGELOG.md](CHANGELOG.md)** - Historique des changements
- **[LICENSE](LICENSE)** - Licence du projet (MIT)

## 🎯 Par cas d'usage

### Je veux...

#### ...démarrer le projet pour la première fois
→ [QUICK_START.md](QUICK_START.md)

#### ...comprendre l'architecture
→ [ARCHITECTURE.md](ARCHITECTURE.md)

#### ...implémenter une fonctionnalité
→ [TODO.md](TODO.md) → [DEVELOPMENT.md](DEVELOPMENT.md)

#### ...contribuer au projet
→ [CONTRIBUTING.md](CONTRIBUTING.md)

#### ...débugger un problème
→ [DEVELOPMENT.md](DEVELOPMENT.md) section "Debugging"

#### ...savoir ce qui a été fait
→ [SKELETON_README.md](SKELETON_README.md) ou [SUMMARY.md](SUMMARY.md)

#### ...voir les prochaines étapes
→ [TODO.md](TODO.md)

#### ...comprendre un fichier spécifique
→ [ARCHITECTURE.md](ARCHITECTURE.md) section "Structure du projet"

## 📱 Documentation du code

La documentation du code se trouve directement dans les fichiers source :

```
app/src/main/java/fr/celld/smartalarm/
├── data/              # Couche de données
│   ├── model/         # Modèles (voir Alarm.kt)
│   ├── local/         # Base de données Room
│   ├── repository/    # Repositories
│   └── preferences/   # DataStore
├── service/           # Services système
├── ui/                # Interface utilisateur
│   ├── screens/       # Écrans Compose
│   ├── viewmodel/     # ViewModels
│   └── navigation/    # Navigation
└── MainActivity.kt    # Point d'entrée
```

## 🔗 Liens utiles

### Documentation externe
- [Android Developers](https://developer.android.com)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Material Design 3](https://m3.material.io)

### Ressources spécifiques
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [AlarmManager Best Practices](https://developer.android.com/training/scheduling/alarms)
- [StateFlow Guide](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)

## 💡 Conseil

Tous les fichiers markdown peuvent être lus directement sur GitHub avec une mise en forme appropriée, ou dans n'importe quel éditeur Markdown.

## 📞 Besoin d'aide ?

Si vous ne trouvez pas l'information que vous cherchez :

1. Consultez la section pertinente ci-dessus
2. Utilisez la recherche dans les fichiers (Ctrl+F dans votre éditeur)
3. Créez une issue sur GitHub
4. Consultez les [Discussions GitHub](https://github.com/VOTRE_USERNAME/SmartAlarm/discussions)

---

**Dernière mise à jour :** 16 janvier 2026  
**Version du squelette :** 0.1.0
