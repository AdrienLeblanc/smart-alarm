# Guide de développement SmartAlarm

## Démarrage rapide

### 1. Synchroniser le projet
Après avoir récupéré le code, synchronisez les dépendances Gradle :
- Ouvrez le projet dans Android Studio
- Cliquez sur "Sync Project with Gradle Files"
- Attendez que toutes les dépendances soient téléchargées

### 2. Compiler l'application
```bash
./gradlew assembleDebug
```

### 3. Lancer l'application
- Connectez un appareil Android ou démarrez un émulateur
- Cliquez sur "Run" dans Android Studio

## Ajouter une nouvelle alarme

### Backend (ViewModel et Repository)
Le code est déjà en place pour créer et gérer les alarmes. Le flux est :
```
AlarmEditViewModel → AlarmRepository → AlarmDao → Room Database
```

### Frontend (UI)
Pour personnaliser l'UI d'édition d'alarme, modifiez :
- `ui/screens/AlarmEditScreen.kt`

## Implémenter un nouveau capteur de détection

1. **Ajouter le type dans DetectionMethod**
```kotlin
// data/model/Alarm.kt
enum class DetectionMethod {
    MANUAL,
    MOTION_SENSOR,
    LIGHT_SENSOR,
    SOUND_SENSOR,
    ACCELEROMETER,
    NOUVEAU_CAPTEUR  // Ajoutez ici
}
```

2. **Créer un service de détection**
```kotlin
// service/SensorDetectionService.kt
class SensorDetectionService(private val context: Context) {
    fun detectUserAwake(method: DetectionMethod): Boolean {
        return when (method) {
            DetectionMethod.MOTION_SENSOR -> checkMotionSensor()
            DetectionMethod.LIGHT_SENSOR -> checkLightSensor()
            // ...
        }
    }
}
```

3. **Intégrer dans AlarmService**
```kotlin
// service/AlarmService.kt
val isAwake = sensorDetectionService.detectUserAwake(alarm.detectionMethod)
if (isAwake) {
    // Annuler l'alarme automatiquement
} else {
    // Faire sonner l'alarme
}
```

## Ajouter un nouveau paramètre

1. **Ajouter dans AppPreferences**
```kotlin
// data/preferences/AppPreferences.kt
companion object {
    private val NOUVEAU_PARAMETRE = booleanPreferencesKey("nouveau_parametre")
}

val nouveauParametre: Flow<Boolean> = context.dataStore.data.map { preferences ->
    preferences[NOUVEAU_PARAMETRE] ?: false
}

suspend fun setNouveauParametre(value: Boolean) {
    context.dataStore.edit { preferences ->
        preferences[NOUVEAU_PARAMETRE] = value
    }
}
```

2. **Exposer dans SettingsViewModel**
```kotlin
// ui/viewmodel/SettingsViewModel.kt
val nouveauParametre: StateFlow<Boolean> = preferences.nouveauParametre
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

fun setNouveauParametre(value: Boolean) {
    viewModelScope.launch {
        preferences.setNouveauParametre(value)
    }
}
```

3. **Afficher dans SettingsScreen**
```kotlin
// ui/screens/SettingsScreen.kt
val nouveauParametre by viewModel.nouveauParametre.collectAsState()

SettingSwitch(
    title = "Nouveau paramètre",
    subtitle = "Description du paramètre",
    checked = nouveauParametre,
    onCheckedChange = { viewModel.setNouveauParametre(it) }
)
```

## Tests

### Tests unitaires
Créez des tests pour les ViewModels et Repositories :
```kotlin
// test/java/fr/celld/smartalarm/viewmodel/AlarmListViewModelTest.kt
class AlarmListViewModelTest {
    @Test
    fun `toggle alarm should update enabled state`() = runTest {
        // Arrange
        val repository = FakeAlarmRepository()
        val viewModel = AlarmListViewModel(repository)
        
        // Act
        viewModel.toggleAlarm(1L, true)
        
        // Assert
        assertTrue(repository.getAlarmById(1L)?.isEnabled == true)
    }
}
```

### Tests d'intégration
Testez les interactions UI avec Compose :
```kotlin
// androidTest/java/fr/celld/smartalarm/AlarmListScreenTest.kt
@Test
fun clickOnAlarm_opensEditScreen() {
    composeTestRule.setContent {
        AlarmListScreen(viewModel, onAddAlarm = {}, onEditAlarm = {})
    }
    
    composeTestRule.onNodeWithText("07:00").performClick()
    // Vérifier la navigation
}
```

## Debugging

### Logs Room
Pour voir les requêtes SQL générées par Room :
```kotlin
Room.databaseBuilder(...)
    .setQueryCallback({ sqlQuery, bindArgs ->
        Log.d("RoomQuery", "SQL: $sqlQuery")
    }, Executors.newSingleThreadExecutor())
    .build()
```

### Inspection de la base de données
Dans Android Studio :
- View → Tool Windows → App Inspection
- Sélectionnez l'onglet "Database Inspector"
- Explorez la table "alarms"

### Tester les alarmes
Pour tester rapidement sans attendre :
```kotlin
// Créez une alarme dans 1 minute au lieu de l'heure choisie
val testTime = System.currentTimeMillis() + 60_000
```

## Migration de base de données

Quand vous modifiez l'entité Alarm :

1. **Incrémenter la version**
```kotlin
@Database(entities = [Alarm::class], version = 2)  // 1 → 2
```

2. **Créer une migration**
```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE alarms ADD COLUMN new_column TEXT NOT NULL DEFAULT ''")
    }
}

Room.databaseBuilder(...)
    .addMigrations(MIGRATION_1_2)
    .build()
```

## Optimisations

### Performance
- Les `Flow` sont automatiquement annulés quand l'UI disparaît
- `StateFlow` garde en mémoire la dernière valeur
- Room utilise un thread pool pour les requêtes

### Batterie
- Les alarmes utilisent `setAlarmClock()` pour être exactes
- Le service passe en foreground seulement quand l'alarme sonne
- Les capteurs ne sont activés que lorsque nécessaire

## Ressources utiles

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [AlarmManager](https://developer.android.com/reference/android/app/AlarmManager)
- [Material Design 3](https://m3.material.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
