# Architecture de SmartAlarm

## Vue d'ensemble

SmartAlarm est une application Android moderne utilisant Jetpack Compose et suivant l'architecture MVVM (Model-View-ViewModel) recommandée par Google.

## Structure du projet

```
fr.celld.smartalarm/
├── data/                          # Couche de données
│   ├── local/                    # Base de données locale
│   │   ├── AlarmDatabase.kt      # Base de données Room
│   │   ├── AlarmDao.kt           # DAO pour les opérations CRUD
│   │   └── Converters.kt         # Convertisseurs de types pour Room
│   ├── model/                    # Modèles de données
│   │   └── Alarm.kt              # Entité Alarm et DetectionMethod
│   ├── preferences/              # Préférences de l'application
│   │   └── AppPreferences.kt     # DataStore pour les paramètres
│   └── repository/               # Repositories
│       └── AlarmRepository.kt    # Repository pour les alarmes
├── service/                      # Services et tâches en arrière-plan
│   ├── AlarmScheduler.kt         # Planification des alarmes avec AlarmManager
│   ├── AlarmReceiver.kt          # BroadcastReceiver pour les alarmes
│   └── AlarmService.kt           # Service pour gérer les alarmes qui sonnent
├── ui/                           # Interface utilisateur
│   ├── navigation/               # Navigation
│   │   └── NavGraph.kt           # Définition des routes
│   ├── screens/                  # Écrans Compose
│   │   ├── AlarmListScreen.kt    # Liste des alarmes
│   │   ├── AlarmEditScreen.kt    # Création/édition d'alarme
│   │   └── SettingsScreen.kt     # Paramètres
│   ├── theme/                    # Thème Material3
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── viewmodel/                # ViewModels
│       ├── AlarmListViewModel.kt
│       ├── AlarmEditViewModel.kt
│       └── SettingsViewModel.kt
└── MainActivity.kt               # Point d'entrée de l'application
```

## Technologies utilisées

### Core
- **Kotlin** : Langage de programmation principal
- **Jetpack Compose** : Framework UI déclaratif moderne
- **Material Design 3** : Design system de Google

### Architecture
- **MVVM** : Pattern architectural (Model-View-ViewModel)
- **Repository Pattern** : Abstraction de la source de données
- **StateFlow** : Gestion de l'état réactif

### Persistence
- **Room** : Base de données SQLite avec ORM
- **DataStore** : Stockage clé-valeur pour les préférences (remplacement de SharedPreferences)

### Navigation
- **Navigation Compose** : Navigation entre les écrans

### Asynchronisme
- **Kotlin Coroutines** : Programmation asynchrone
- **Flow** : Flux de données réactifs

### Services système
- **AlarmManager** : Planification des alarmes exactes
- **Foreground Service** : Service pour faire sonner l'alarme
- **BroadcastReceiver** : Réception des alarmes planifiées
- **WorkManager** : Tâches en arrière-plan (pour les futures fonctionnalités)

## Flux de données

### Création d'une alarme
1. L'utilisateur remplit le formulaire dans `AlarmEditScreen`
2. `AlarmEditViewModel` collecte les données
3. Sauvegarde via `AlarmRepository` → `AlarmDao` → `AlarmDatabase`
4. `AlarmScheduler` planifie l'alarme avec `AlarmManager`

### Déclenchement d'une alarme
1. `AlarmManager` déclenche `AlarmReceiver` à l'heure programmée
2. `AlarmReceiver` démarre `AlarmService` (foreground service)
3. `AlarmService` :
   - Charge les détails de l'alarme depuis la base de données
   - Vérifie le capteur de détection si configuré
   - Joue la sonnerie et vibre
   - Affiche une notification/activité pour arrêter l'alarme

### Affichage des alarmes
1. `AlarmListScreen` observe le StateFlow du `AlarmListViewModel`
2. `AlarmListViewModel` collecte les données du `AlarmRepository`
3. `AlarmRepository` expose un Flow depuis `AlarmDao`
4. Toute modification dans la base de données déclenche automatiquement une mise à jour de l'UI

## Fonctionnalités principales

### ✅ Implémentées dans le squelette
- Création et édition d'alarmes
- Liste des alarmes avec activation/désactivation
- Paramètres de répétition (jours de la semaine)
- Choix de la méthode de détection
- Paramètres de l'application (notifications, thème)
- Structure de base pour la planification des alarmes

### 🚧 À implémenter
- Time Picker Material3 dans l'écran d'édition
- Sélecteur de sonnerie
- Gestion de la sonnerie et vibration dans AlarmService
- Détection par capteurs (mouvement, lumière, son, accéléromètre)
- Activité plein écran pour arrêter l'alarme
- Gestion du mode Snooze
- Notifications pour les alarmes à venir
- Réactivation des alarmes après redémarrage du téléphone
- Tests unitaires et d'intégration

## Permissions Android

L'application nécessite les permissions suivantes :

- `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM` : Pour planifier des alarmes exactes
- `VIBRATE` : Pour faire vibrer le téléphone
- `WAKE_LOCK` : Pour réveiller l'appareil
- `FOREGROUND_SERVICE` : Pour le service d'alarme
- `POST_NOTIFICATIONS` : Pour afficher des notifications
- `RECEIVE_BOOT_COMPLETED` : Pour réactiver les alarmes après redémarrage

## Bonnes pratiques implémentées

- ✅ Séparation des responsabilités (MVVM)
- ✅ Injection de dépendances manuelle (pour l'instant)
- ✅ Utilisation de Flow et StateFlow pour la réactivité
- ✅ Gestion d'état avec ViewModel
- ✅ Base de données Room avec coroutines
- ✅ Navigation type-safe
- ✅ Material Design 3
- ✅ Edge-to-edge UI

## Prochaines étapes recommandées

1. **Implémenter Dependency Injection** : Utiliser Hilt ou Koin
2. **Ajouter les capteurs** : Implémenter les méthodes de détection
3. **Implémenter le Time Picker** : Utiliser Material3 TimePicker
4. **Créer l'activité d'alarme** : Écran plein écran pour arrêter/snoozer
5. **Ajouter les tests** : Unit tests et UI tests
6. **Gérer les notifications** : Notifications riches avec actions
7. **Persistance après reboot** : BootReceiver pour replanifier les alarmes
8. **Améliorer l'UX** : Animations, transitions, haptic feedback

## Configuration requise

- **minSdk** : 24 (Android 7.0 Nougat)
- **targetSdk** : 36
- **compileSdk** : 36
- **Kotlin** : 2.0.21
- **AGP** : 9.0.0
