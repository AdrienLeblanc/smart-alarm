# 🚀 Quick Start - SmartAlarm

## Démarrage en 5 minutes

### 1. Ouvrir le projet
```
Android Studio → Open → Sélectionner le dossier SmartAlarm
```

### 2. Synchroniser Gradle
Android Studio va automatiquement synchroniser le projet. Si ce n'est pas le cas :
```
File → Sync Project with Gradle Files
```

### 3. Build le projet
```
Build → Make Project
```
Ou avec le raccourci : `Ctrl+F9` (Windows/Linux) ou `Cmd+F9` (Mac)

### 4. Lancer l'application
- Connecter un appareil Android ou démarrer un émulateur
- Cliquer sur le bouton "Run" (▶️) ou `Shift+F10`

---

## 📱 Tester l'application

### Créer une alarme
1. Cliquer sur le bouton `+` (FAB)
2. L'écran d'édition s'ouvre
3. Modifier les paramètres (l'heure n'est pas encore fonctionnelle)
4. Cliquer sur "Enregistrer"
5. L'alarme apparaît dans la liste

### Activer/désactiver une alarme
- Utiliser le switch sur la carte de l'alarme

### Supprimer une alarme
- Cliquer sur l'icône poubelle
- Confirmer dans la boîte de dialogue

### Modifier une alarme
- Cliquer sur la carte de l'alarme
- L'écran d'édition s'ouvre avec les données chargées

### Accéder aux paramètres
- Cliquer sur l'icône "Paramètres" dans la barre de navigation

---

## ⚠️ Limitations actuelles

### ❌ Non implémenté
- **Time Picker** : L'heure est fixe (placeholder)
- **Sonnerie** : L'alarme ne sonne pas encore
- **Détection de capteurs** : Pas encore implémentée
- **Planification réelle** : AlarmManager pas encore connecté
- **Notifications** : Pas encore de notifications

### ✅ Fonctionnel
- Base de données Room (création, lecture, mise à jour, suppression)
- Navigation entre les écrans
- Sauvegarde des paramètres dans DataStore
- Interface utilisateur complète
- Gestion des jours de répétition
- Choix de la méthode de détection (UI seulement)

---

## 🛠️ Débugger

### Voir la base de données
Dans Android Studio :
1. `View → Tool Windows → App Inspection`
2. Sélectionner l'onglet `Database Inspector`
3. Sélectionner votre app
4. Explorer la table `alarms`

### Voir les logs
Dans Android Studio :
1. `View → Tool Windows → Logcat`
2. Filtrer par package : `fr.celld.smartalarm`

### Ajouter des logs
```kotlin
import android.util.Log

Log.d("AlarmListViewModel", "Alarm toggled: $alarmId")
Log.e("AlarmService", "Error: ${e.message}")
```

---

## 📂 Fichiers importants à connaître

### Point d'entrée
```
MainActivity.kt - Lance l'application et initialise les dépendances
```

### Modèle de données
```
data/model/Alarm.kt - Définition de l'alarme
```

### Base de données
```
data/local/AlarmDatabase.kt - Configuration Room
data/local/AlarmDao.kt - Requêtes SQL
```

### Écrans principaux
```
ui/screens/AlarmListScreen.kt - Liste des alarmes
ui/screens/AlarmEditScreen.kt - Édition d'alarme
ui/screens/SettingsScreen.kt - Paramètres
```

### ViewModels
```
ui/viewmodel/AlarmListViewModel.kt - Logique liste
ui/viewmodel/AlarmEditViewModel.kt - Logique édition
ui/viewmodel/SettingsViewModel.kt - Logique paramètres
```

---

## 🔧 Premiers pas de développement

### Tâche 1 : Implémenter le Time Picker
**Difficulté :** Moyenne  
**Fichier :** `ui/screens/AlarmEditScreen.kt`  
**Ligne :** ~58-80

```kotlin
// Remplacer TimePickerSection par :
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerSection(
    hour: Int,
    minute: Int,
    onTimeChanged: (Int, Int) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = true
    )
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Heure de l'alarme", style = MaterialTheme.typography.titleMedium)
        
        TextButton(onClick = { showTimePicker = true }) {
            Text(
                text = String.format("%02d:%02d", hour, minute),
                style = MaterialTheme.typography.displayLarge
            )
        }
    }
    
    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onTimeChanged(
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        text = content
    )
}
```

### Tâche 2 : Connecter AlarmScheduler
**Difficulté :** Facile  
**Fichiers :** 
- `ui/viewmodel/AlarmListViewModel.kt`
- `ui/viewmodel/AlarmEditViewModel.kt`

**Problème :** ViewModels ont besoin du Context

**Solution temporaire :**
```kotlin
// Dans MainActivity.kt
val alarmScheduler = AlarmScheduler(applicationContext)

// Passer aux ViewModels
val alarmListViewModel = AlarmListViewModel(repository, alarmScheduler)
val alarmEditViewModel = AlarmEditViewModel(repository, alarmScheduler)
```

**Modifier les ViewModels :**
```kotlin
class AlarmListViewModel(
    private val repository: AlarmRepository,
    private val scheduler: AlarmScheduler
) : ViewModel() {
    
    fun toggleAlarm(alarmId: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleAlarm(alarmId, enabled)
            val alarm = repository.getAlarmById(alarmId)
            alarm?.let {
                if (enabled) {
                    scheduler.scheduleAlarm(it)
                } else {
                    scheduler.cancelAlarm(alarmId)
                }
            }
        }
    }
}
```

### Tâche 3 : Faire sonner l'alarme
**Difficulté :** Moyenne  
**Fichier :** `service/AlarmService.kt`

Voir le fichier `TODO.md` section 2 pour les détails.

---

## 🎓 Ressources d'apprentissage

### Jetpack Compose
- [Compose Basics](https://developer.android.com/jetpack/compose/tutorial)
- [State in Compose](https://developer.android.com/jetpack/compose/state)
- [Material3 Components](https://developer.android.com/jetpack/compose/components)

### Room Database
- [Room Guide](https://developer.android.com/training/data-storage/room)
- [Room with Flow](https://developer.android.com/training/data-storage/room/async-queries)

### MVVM Architecture
- [Android Architecture](https://developer.android.com/topic/architecture)
- [ViewModel Guide](https://developer.android.com/topic/libraries/architecture/viewmodel)

### AlarmManager
- [Schedule Alarms](https://developer.android.com/training/scheduling/alarms)
- [Background Work Guide](https://developer.android.com/guide/background)

---

## 💡 Astuces

### Rebuild le projet en cas de problème
```
Build → Clean Project
Build → Rebuild Project
```

### Invalider les caches
```
File → Invalidate Caches → Invalidate and Restart
```

### Désinstaller et réinstaller l'app
Pour réinitialiser la base de données :
```
adb uninstall fr.celld.smartalarm
```
Puis relancer avec Run

### Voir les requêtes SQL
Ajouter dans `AlarmDatabase.kt` :
```kotlin
Room.databaseBuilder(...)
    .setQueryCallback({ sqlQuery, _ ->
        Log.d("RoomQuery", sqlQuery)
    }, Executors.newSingleThreadExecutor())
    .build()
```

---

## 🐛 Problèmes courants

### Gradle sync échoue
- Vérifier la connexion internet
- Vérifier que Java est installé
- Redémarrer Android Studio

### L'app crash au démarrage
- Vérifier les logs dans Logcat
- Vérifier que toutes les dépendances sont synchronisées
- Clean & Rebuild

### Room database errors
- Vérifier que KSP est bien configuré
- Rebuild le projet
- Vérifier les annotations @Entity, @Dao, @Database

### Navigation ne fonctionne pas
- Vérifier les routes dans NavGraph.kt
- Vérifier les arguments de navigation
- Vérifier les imports androidx.navigation.compose

---

## ✅ Checklist avant de commencer à coder

- [ ] Projet ouvert dans Android Studio
- [ ] Gradle synchronisé sans erreur
- [ ] Build réussi
- [ ] App lance sur émulateur/appareil
- [ ] Base de données fonctionne (créer une alarme de test)
- [ ] Navigation fonctionne (passer d'un écran à l'autre)
- [ ] Lu ARCHITECTURE.md
- [ ] Lu TODO.md

---

**Prêt à coder ! 🚀**

Si vous avez des questions ou rencontrez des problèmes, consultez :
- `ARCHITECTURE.md` pour comprendre la structure
- `DEVELOPMENT.md` pour les guides de développement
- `TODO.md` pour les tâches à faire
- `SKELETON_README.md` pour une vue d'ensemble

Bon développement ! 💪
