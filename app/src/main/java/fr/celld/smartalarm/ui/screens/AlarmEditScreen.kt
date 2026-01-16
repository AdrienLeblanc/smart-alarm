package fr.celld.smartalarm.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.celld.smartalarm.data.model.DetectionMethod
import fr.celld.smartalarm.ui.viewmodel.AlarmEditViewModel

/**
 * Écran d'édition/création d'une alarme
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmEditScreen(
    viewModel: AlarmEditViewModel,
    alarmId: Long?,
    onNavigateBack: () -> Unit
) {
    val alarm by viewModel.alarm.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    LaunchedEffect(alarmId) {
        if (alarmId != null && alarmId > 0) {
            viewModel.loadAlarm(alarmId)
        } else {
            viewModel.createNewAlarm()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (alarmId == null || alarmId == 0L) "Nouvelle alarme" else "Modifier l'alarme") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.saveAlarm(onNavigateBack)
                        },
                        enabled = !isSaving
                    ) {
                        Text("Enregistrer")
                    }
                }
            )
        }
    ) { paddingValues ->
        alarm?.let { currentAlarm ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sélecteur d'heure
                TimePickerSection(
                    hour = currentAlarm.hour,
                    minute = currentAlarm.minute,
                    onTimeChanged = { hour, minute ->
                        viewModel.updateTime(hour, minute)
                    }
                )

                HorizontalDivider()

                // Label
                OutlinedTextField(
                    value = currentAlarm.label,
                    onValueChange = { viewModel.updateLabel(it) },
                    label = { Text("Libellé") },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                // Jours de répétition
                RepeatDaysSection(
                    selectedDays = currentAlarm.repeatDays,
                    onDayToggled = { day -> viewModel.toggleRepeatDay(day) }
                )

                HorizontalDivider()

                // Vibration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Vibration", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = currentAlarm.vibrate,
                        onCheckedChange = { viewModel.updateVibrate(it) }
                    )
                }

                HorizontalDivider()

                // Méthode de détection
                DetectionMethodSection(
                    selectedMethod = currentAlarm.detectionMethod,
                    onMethodSelected = { viewModel.updateDetectionMethod(it) }
                )

                HorizontalDivider()

                // Snooze
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mode Snooze", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "${currentAlarm.snoozeDuration} minutes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = currentAlarm.snoozeEnabled,
                        onCheckedChange = { viewModel.updateSnooze(it, currentAlarm.snoozeDuration) }
                    )
                }
            }
        }
    }
}

@Composable
fun TimePickerSection(
    hour: Int,
    minute: Int,
    onTimeChanged: (Int, Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Heure de l'alarme",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Affichage simple de l'heure (un vrai Time Picker serait mieux)
        Text(
            text = String.format("%02d:%02d", hour, minute),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )

        // TODO: Implémenter un vrai Time Picker avec Material3
        Text(
            text = "Tapez pour modifier",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RepeatDaysSection(
    selectedDays: Set<Int>,
    onDayToggled: (Int) -> Unit
) {
    Column {
        Text(
            text = "Répétition",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val dayNames = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dayNames.forEachIndexed { index, day ->
                FilterChip(
                    selected = selectedDays.contains(index),
                    onClick = { onDayToggled(index) },
                    label = { Text(day) }
                )
            }
        }
    }
}

@Composable
fun DetectionMethodSection(
    selectedMethod: DetectionMethod,
    onMethodSelected: (DetectionMethod) -> Unit
) {
    Column {
        Text(
            text = "Méthode de détection de réveil",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        DetectionMethod.entries.forEach { method ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedMethod == method,
                    onClick = { onMethodSelected(method) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = getDetectionMethodName(method),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = getDetectionMethodDescription(method),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

fun getDetectionMethodName(method: DetectionMethod): String = when (method) {
    DetectionMethod.MANUAL -> "Manuel"
    DetectionMethod.MOTION_SENSOR -> "Capteur de mouvement"
    DetectionMethod.LIGHT_SENSOR -> "Capteur de luminosité"
    DetectionMethod.SOUND_SENSOR -> "Capteur de son"
    DetectionMethod.ACCELEROMETER -> "Accéléromètre"
}

fun getDetectionMethodDescription(method: DetectionMethod): String = when (method) {
    DetectionMethod.MANUAL -> "Désactivation manuelle uniquement"
    DetectionMethod.MOTION_SENSOR -> "Détecte les mouvements dans la pièce"
    DetectionMethod.LIGHT_SENSOR -> "Détecte si la lumière est allumée"
    DetectionMethod.SOUND_SENSOR -> "Détecte les bruits ambiants"
    DetectionMethod.ACCELEROMETER -> "Détecte les mouvements du téléphone"
}
