# 🔔 SmartAlarm

> Une application d'alarme intelligente pour Android qui s'adapte à votre réveil

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-2024.09-green.svg)](https://developer.android.com/jetpack/compose)
[![Material3](https://img.shields.io/badge/Material-3-orange.svg)](https://m3.material.io)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📱 À propos

SmartAlarm est une application d'alarme révolutionnaire qui utilise des capteurs pour détecter si vous êtes déjà réveillé, évitant ainsi les réveils inutiles. Fini les alarmes qui sonnent alors que vous êtes déjà debout depuis 10 minutes !

### ✨ Fonctionnalités principales

- 🎯 **Détection intelligente** : Utilise des capteurs pour savoir si vous êtes réveillé
- 📅 **Alarmes répétitives** : Configurez vos alarmes pour des jours spécifiques
- 🔊 **Personnalisation** : Choisissez votre sonnerie et vibration
- 💤 **Mode Snooze** : Retardez votre alarme facilement
- 🌓 **Thème adaptatif** : Supporte les modes clair et sombre
- 📱 **Interface moderne** : Design Material 3 avec Jetpack Compose

### 🎛️ Méthodes de détection

- **Manuel** : Désactivation manuelle classique
- **Capteur de mouvement** : Détecte les mouvements dans la pièce
- **Capteur de luminosité** : Détecte si la lumière est allumée
- **Capteur de son** : Détecte les bruits ambiants
- **Accéléromètre** : Détecte les mouvements du téléphone

## 🚀 Installation

### Prérequis

- Android Studio (dernière version)
- JDK 11 ou supérieur
- Android SDK 24+ (Android 7.0 Nougat)

### Étapes

1. **Cloner le repository**
```bash
git clone https://github.com/VOTRE_USERNAME/SmartAlarm.git
cd SmartAlarm
```

2. **Ouvrir dans Android Studio**
```
File → Open → Sélectionner le dossier SmartAlarm
```

3. **Synchroniser Gradle**
```
File → Sync Project with Gradle Files
```

4. **Lancer l'application**
```
Run → Run 'app' (ou Shift+F10)
```

## 📖 Documentation

- 📘 **[Quick Start](QUICK_START.md)** - Démarrer rapidement
- 🏗️ **[Architecture](ARCHITECTURE.md)** - Architecture du projet
- 💻 **[Development Guide](DEVELOPMENT.md)** - Guide de développement
- ✅ **[TODO](TODO.md)** - Liste des tâches
- 📦 **[Skeleton Overview](SKELETON_README.md)** - Vue d'ensemble du squelette

## 🏗️ Architecture

SmartAlarm suit l'architecture **MVVM** (Model-View-ViewModel) recommandée par Google avec :

- **UI Layer** : Jetpack Compose + Material3
- **Domain Layer** : ViewModels + Use Cases
- **Data Layer** : Room Database + DataStore + Repository

```
┌─────────────────────────────────────┐
│           UI (Compose)              │
├─────────────────────────────────────┤
│     ViewModels (StateFlow)          │
├─────────────────────────────────────┤
│      Repository Pattern             │
├─────────────────────────────────────┤
│  Room Database + DataStore          │
└─────────────────────────────────────┘
```

## 🛠️ Technologies

### Core
- **Kotlin** 2.0.21 - Langage principal
- **Jetpack Compose** - UI déclarative
- **Material Design 3** - Design system

### Jetpack
- **Room** 2.6.1 - Base de données locale
- **DataStore** 1.0.0 - Préférences
- **Navigation Compose** 2.7.7 - Navigation
- **ViewModel** - Gestion de l'état
- **WorkManager** 2.9.0 - Tâches en arrière-plan

### Asynchrone
- **Kotlin Coroutines** 1.7.3 - Programmation asynchrone
- **Flow** - Flux de données réactifs

## 📸 Captures d'écran

_Coming soon..._

## 🔧 Configuration

### Permissions

L'application nécessite les permissions suivantes :

- `SCHEDULE_EXACT_ALARM` - Alarmes exactes
- `VIBRATE` - Vibration
- `WAKE_LOCK` - Réveiller l'appareil
- `FOREGROUND_SERVICE` - Service foreground
- `POST_NOTIFICATIONS` - Notifications
- `RECEIVE_BOOT_COMPLETED` - Redémarrage

## 🤝 Contribution

Les contributions sont les bienvenues ! Consultez [CONTRIBUTING.md](CONTRIBUTING.md) pour plus d'informations.

### Comment contribuer

1. Fork le projet
2. Créez votre branche (`git checkout -b feature/AmazingFeature`)
3. Committez vos changements (`git commit -m 'feat: Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

## 📝 Roadmap

- [x] Squelette de base
- [ ] Time Picker Material3
- [ ] Planification d'alarmes fonctionnelle
- [ ] Détection par capteurs
- [ ] Sélecteur de sonnerie
- [ ] Notifications
- [ ] Tests unitaires
- [ ] Version 1.0 release

Consultez [TODO.md](TODO.md) pour la liste complète.

## 🐛 Bugs et suggestions

Trouvé un bug ? Vous avez une suggestion ?

- 🐞 [Signaler un bug](https://github.com/VOTRE_USERNAME/SmartAlarm/issues/new?template=bug_report.md)
- 💡 [Proposer une fonctionnalité](https://github.com/VOTRE_USERNAME/SmartAlarm/issues/new?template=feature_request.md)

## 📄 Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.

## 👥 Auteurs

- **Celldwaller** - *Développement initial* - [GitHub](https://github.com/Celldwaller)

Voir aussi la liste des [contributeurs](CONTRIBUTORS.md) qui ont participé à ce projet.

## 🙏 Remerciements

- [Material Design 3](https://m3.material.io) pour le design system
- [Jetpack Compose](https://developer.android.com/jetpack/compose) pour le framework UI
- La communauté Android pour les ressources et le support

## 📞 Support

- 📧 Email : support@smartalarm.example.com
- 💬 [Discussions GitHub](https://github.com/VOTRE_USERNAME/SmartAlarm/discussions)
- 📚 [Documentation](https://github.com/VOTRE_USERNAME/SmartAlarm/wiki)

---

<p align="center">
  Fait avec ❤️ par la communauté SmartAlarm
</p>

<p align="center">
  <sub>⭐ N'oubliez pas de donner une étoile si ce projet vous plaît !</sub>
</p>
