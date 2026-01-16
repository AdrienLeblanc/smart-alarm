# TODO SmartAlarm

## 🔴 Critiques (MVP)

### 1. Implémenter le Time Picker
- [ ] Remplacer le placeholder dans `AlarmEditScreen.kt`
- [ ] Utiliser Material3 TimePicker
- [ ] Gérer l'affichage 12h/24h selon les préférences système
- [ ] Connecter au ViewModel pour la sauvegarde

**Fichiers concernés :**
- `ui/screens/AlarmEditScreen.kt` (ligne ~58-80)

**Ressources :**
- [Material3 TimePicker](https://developer.android.com/jetpack/compose/components/timepicker)

---

### 2. Compléter AlarmService
- [ ] Charger l'alarme depuis Room
- [ ] Jouer la sonnerie depuis l'URI
- [ ] Gérer la vibration
- [ ] Créer une notification avec actions (Arrêter/Snooze)
- [ ] Lancer une activité plein écran

**Fichiers concernés :**
- `service/AlarmService.kt`
- Créer `ui/screens/AlarmRingingScreen.kt`

**Détails :**
```kotlin
// AlarmService.kt - À implémenter
private fun playRingtone(uri: String) {
    val ringtone = RingtoneManager.getRingtone(this, Uri.parse(uri))
    ringtone.play()
}

private fun vibratePhone() {
    val vibrator = getSystemService(Vibrator::class.java)
    val pattern = longArrayOf(0, 1000, 500, 1000)
    vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
}
```

---

### 3. Compléter AlarmScheduler
- [ ] Intégrer l'appel dans `AlarmListViewModel.toggleAlarm()`
- [ ] Intégrer l'appel dans `AlarmEditViewModel.saveAlarm()`
- [ ] Gérer les alarmes répétitives
- [ ] Tester sur Android 12+ (permission SCHEDULE_EXACT_ALARM)

**Fichiers concernés :**
- `ui/viewmodel/AlarmListViewModel.kt` (ligne ~27)
- `ui/viewmodel/AlarmEditViewModel.kt` (ligne ~108)
- `service/AlarmScheduler.kt`

**Code à ajouter :**
```kotlin
// Dans AlarmListViewModel
fun toggleAlarm(alarmId: Long, enabled: Boolean) {
    viewModelScope.launch {
        repository.toggleAlarm(alarmId, enabled)
        val alarm = repository.getAlarmById(alarmId)
        alarm?.let {
            val scheduler = AlarmScheduler(context) // Nécessite Context
            if (enabled) {
                scheduler.scheduleAlarm(it)
            } else {
                scheduler.cancelAlarm(alarmId)
            }
        }
    }
}
```

---

### 4. Gestion du Context dans ViewModels
- [ ] Implémenter AndroidViewModel ou
- [ ] Passer le Context via constructeur ou
- [ ] Migrer vers Hilt pour l'injection de dépendances

**Options :**
```kotlin
// Option 1: AndroidViewModel
class AlarmListViewModel(
    application: Application,
    private val repository: AlarmRepository
) : AndroidViewModel(application) {
    private val context get() = getApplication<Application>()
}

// Option 2: Hilt (recommandé)
@HiltViewModel
class AlarmListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AlarmRepository
) : ViewModel()
```

---

## 🟡 Importantes (Post-MVP)

### 5. Sélecteur de sonnerie
- [ ] Intent pour choisir une sonnerie
- [ ] Aperçu de la sonnerie
- [ ] Sonnerie par défaut du système
- [ ] Stocker l'URI dans l'alarme

**Fichier à créer :**
- `ui/components/RingtonePicker.kt`

---

### 6. Implémenter les capteurs de détection

#### Motion Sensor
- [ ] Accès au capteur de mouvement
- [ ] Détection de mouvement dans la pièce
- [ ] Seuil configurable

#### Light Sensor
- [ ] Accès au capteur de luminosité
- [ ] Détection lumière allumée
- [ ] Seuil en lux

#### Accelerometer
- [ ] Accès à l'accéléromètre
- [ ] Détection téléphone bougé
- [ ] Sensibilité configurable

#### Sound Sensor
- [ ] Accès au microphone (permission)
- [ ] Détection de bruit ambiant
- [ ] Seuil en décibels

**Fichier à créer :**
- `service/SensorDetectionService.kt`

**Permissions à demander :**
```xml
<!-- Dans AndroidManifest.xml -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.BODY_SENSORS" />
```

---

### 7. BootReceiver
- [ ] Créer le BroadcastReceiver
- [ ] Recharger toutes les alarmes activées
- [ ] Replanifier avec AlarmScheduler
- [ ] Tester après redémarrage

**Fichier à créer :**
- `service/BootReceiver.kt`

**Code :**
```kotlin
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Replanifier toutes les alarmes
            val database = AlarmDatabase.getDatabase(context)
            val repository = AlarmRepository(database.alarmDao())
            
            CoroutineScope(Dispatchers.IO).launch {
                repository.getEnabledAlarms().collect { alarms ->
                    val scheduler = AlarmScheduler(context)
                    alarms.forEach { alarm ->
                        scheduler.scheduleAlarm(alarm)
                    }
                }
            }
        }
    }
}
```

**Ajouter dans AndroidManifest.xml :**
```xml
<receiver
    android:name=".service.BootReceiver"
    android:enabled="true"
    android:exported="false">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
    </intent-filter>
</receiver>
```

---

### 8. Notifications avancées
- [ ] Notification pour alarme à venir (30 min avant)
- [ ] Actions dans la notification (Arrêter/Snooze)
- [ ] Notification persistante pendant que l'alarme sonne
- [ ] Style de notification personnalisé

**Fichier à créer :**
- `service/NotificationHelper.kt`

---

### 9. Gestion du Snooze
- [ ] Bouton Snooze dans l'écran d'alarme
- [ ] Replanifier l'alarme dans X minutes
- [ ] Afficher le nombre de snoozes
- [ ] Limite de snoozes configurable

**Code à ajouter dans AlarmService :**
```kotlin
fun snoozeAlarm(alarmId: Long, minutes: Int) {
    val calendar = Calendar.getInstance().apply {
        add(Calendar.MINUTE, minutes)
    }
    // Replanifier
}
```

---

## 🟢 Améliorations (Nice to have)

### 10. Tests
- [ ] Unit tests pour ViewModels
- [ ] Tests d'intégration pour Repository
- [ ] UI tests pour les écrans
- [ ] Tests pour AlarmScheduler

**Fichiers à créer :**
- `test/viewmodel/AlarmListViewModelTest.kt`
- `test/repository/AlarmRepositoryTest.kt`
- `androidTest/AlarmListScreenTest.kt`

---

### 11. Injection de dépendances (Hilt)
- [ ] Ajouter Hilt dans les dépendances
- [ ] Créer les modules Hilt
- [ ] Annoter les ViewModels avec @HiltViewModel
- [ ] Annoter MainActivity avec @AndroidEntryPoint

**Dépendances à ajouter :**
```kotlin
// build.gradle.kts
plugins {
    id("com.google.dagger.hilt.android")
}

dependencies {
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
}
```

---

### 12. Thème dynamique
- [ ] Support de Material You (Android 12+)
- [ ] Couleurs dynamiques basées sur le wallpaper
- [ ] Toggle dans les paramètres

**Code à ajouter dans Theme.kt :**
```kotlin
val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
val colorScheme = when {
    dynamicColor && darkTheme -> dynamicDarkColorScheme(context)
    dynamicColor && !darkTheme -> dynamicLightColorScheme(context)
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
}
```

---

### 13. Animations
- [ ] Transitions entre écrans
- [ ] Animation du FAB
- [ ] Animation des cartes d'alarmes
- [ ] Haptic feedback

---

### 14. Statistiques
- [ ] Nombre d'alarmes sonnées
- [ ] Temps moyen de réveil
- [ ] Taux de snooze
- [ ] Graphiques

**Fichiers à créer :**
- `ui/screens/StatisticsScreen.kt`
- `data/model/AlarmHistory.kt`

---

### 15. Backup & Restore
- [ ] Export des alarmes (JSON)
- [ ] Import des alarmes
- [ ] Backup automatique vers Drive

---

### 16. Widget
- [ ] Widget pour la prochaine alarme
- [ ] Quick toggle on/off
- [ ] Affichage sur lock screen

---

### 17. Accessibilité
- [ ] Content descriptions complètes
- [ ] Support TalkBack
- [ ] Taille de texte scalable
- [ ] Contraste élevé

---

### 18. Localisation
- [ ] Strings en français
- [ ] Support multilingue (EN, FR, ES, etc.)
- [ ] Formatage des heures selon la locale

**Fichier à créer :**
- `res/values-fr/strings.xml`

---

## 📋 Checklist de lancement

### Avant la release
- [ ] Toutes les fonctionnalités MVP implémentées
- [ ] Tests passés
- [ ] Pas de crash sur Android 7-14
- [ ] Permissions gérées correctement
- [ ] ProGuard configuré
- [ ] Icône d'application
- [ ] Screenshots pour le Play Store
- [ ] Description et texte marketing
- [ ] Privacy policy
- [ ] Changelog

### Performance
- [ ] Pas de leak mémoire
- [ ] Temps de démarrage < 2s
- [ ] Consommation batterie acceptable
- [ ] Taille APK optimisée

### Sécurité
- [ ] Pas de hardcoded secrets
- [ ] Validation des inputs
- [ ] Gestion des erreurs réseau
- [ ] Code obfusqué

---

## 🎯 Priorités suggérées

1. **Semaine 1** : TODO 1, 2, 3, 4 (MVP fonctionnel)
2. **Semaine 2** : TODO 5, 6, 7 (Fonctionnalités principales)
3. **Semaine 3** : TODO 8, 9, 10 (Robustesse)
4. **Semaine 4** : TODO 11-18 (Améliorations)

---

**Dernière mise à jour :** 16 janvier 2026
