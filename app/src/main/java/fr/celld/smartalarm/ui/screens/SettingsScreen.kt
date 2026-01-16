package fr.celld.smartalarm.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.celld.smartalarm.ui.viewmodel.SettingsViewModel

/**
 * Écran des paramètres de l'application
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val defaultVibrate by viewModel.defaultVibrate.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paramètres") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Section Notifications
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            SettingSwitch(
                title = "Activer les notifications",
                subtitle = "Recevoir des notifications pour les alarmes à venir",
                checked = notificationsEnabled,
                onCheckedChange = { viewModel.setNotificationsEnabled(it) }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Section Apparence
            Text(
                text = "Apparence",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            ThemeSelector(
                selectedTheme = themeMode,
                onThemeSelected = { viewModel.setThemeMode(it) }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Section Alarmes par défaut
            Text(
                text = "Paramètres par défaut",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            SettingSwitch(
                title = "Vibration par défaut",
                subtitle = "Activer la vibration pour les nouvelles alarmes",
                checked = defaultVibrate,
                onCheckedChange = { viewModel.setDefaultVibrate(it) }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Section À propos
            Text(
                text = "À propos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "SmartAlarm",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Version 1.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Une application d'alarme intelligente qui s'adapte à votre réveil.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SettingSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun ThemeSelector(
    selectedTheme: String,
    onThemeSelected: (String) -> Unit
) {
    Column {
        Text(
            text = "Thème",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val themes = listOf(
            "system" to "Système",
            "light" to "Clair",
            "dark" to "Sombre"
        )

        themes.forEach { (value, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedTheme == value,
                    onClick = { onThemeSelected(value) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(label)
            }
        }
    }
}
