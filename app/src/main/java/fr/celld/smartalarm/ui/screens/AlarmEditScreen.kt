package fr.celld.smartalarm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.celld.smartalarm.data.model.DetectionMethod
import fr.celld.smartalarm.ui.viewmodel.AlarmEditViewModel

/**
 * Alarm edit/create screen
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
                // Time picker
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

                // Repeat days
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

                // Detection method
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerSection(
    hour: Int,
    minute: Int,
    onTimeChanged: (Int, Int) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Heure de l'alarme",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Clickable time display
        TextButton(
            onClick = { showTimePicker = true }
        ) {
            Text(
                text = String.format(java.util.Locale.getDefault(), "%02d:%02d", hour, minute),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = "Tapez pour modifier",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    // TimePicker dialog
    if (showTimePicker) {
        TimePickerDialog(
            initialHour = hour,
            initialMinute = minute,
            onDismiss = { showTimePicker = false },
            onConfirm = { selectedHour, selectedMinute ->
                onTimeChanged(selectedHour, selectedMinute)
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(timePickerState.hour, timePickerState.minute)
            }) {
                Text("OK")
            }
        },
        text = {
            TimePicker(
                state = timePickerState
            )
        }
    )
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
