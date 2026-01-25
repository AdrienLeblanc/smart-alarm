package fr.celld.smartalarm.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.celld.smartalarm.data.local.AlarmDatabase
import fr.celld.smartalarm.data.model.Alarm
import fr.celld.smartalarm.service.AlarmScheduler
import fr.celld.smartalarm.service.AlarmService
import fr.celld.smartalarm.ui.theme.SmartAlarmTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class AlarmRingingActivity : ComponentActivity() {

    private var alarmId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alarmId = intent.getLongExtra(AlarmScheduler.ALARM_ID_EXTRA, -1)

        // Configure pour afficher sur l'écran verrouillé
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val keyguardManager = getSystemService(KEYGUARD_SERVICE) as android.app.KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }

        setContent {
            SmartAlarmTheme {
                AlarmRingingScreen(
                    alarmId = alarmId,
                    onStop = { stopAlarm() },
                    onSnooze = { snoozeAlarm() }
                )
            }
        }
    }

    private fun stopAlarm() {
        // Envoyer un broadcast au service pour arrêter l'alarme
        val stopIntent = Intent(AlarmService.ACTION_STOP_ALARM).apply {
            setPackage(packageName)
        }
        sendBroadcast(stopIntent)
        finish()
    }

    private fun snoozeAlarm() {
        // Envoyer un broadcast au service pour snooze
        val snoozeIntent = Intent(AlarmService.ACTION_SNOOZE_ALARM).apply {
            setPackage(packageName)
            putExtra(AlarmScheduler.ALARM_ID_EXTRA, alarmId)
        }
        sendBroadcast(snoozeIntent)
        finish()
    }
}

@Composable
fun AlarmRingingScreen(
    alarmId: Long,
    onStop: () -> Unit,
    onSnooze: () -> Unit
) {
    var alarm by remember { mutableStateOf<Alarm?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(alarmId) {
        if (alarmId != -1L) {
            withContext(Dispatchers.IO) {
                val database = AlarmDatabase.getDatabase(context)
                val loadedAlarm = database.alarmDao().getAlarmById(alarmId)
                withContext(Dispatchers.Main) {
                    alarm = loadedAlarm
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                // Icône d'alarme
                Icon(
                    imageVector = Icons.Filled.Alarm,
                    contentDescription = "Alarme",
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                // Heure
                Text(
                    text = alarm?.let {
                        String.format(Locale.getDefault(), "%02d:%02d", it.hour, it.minute)
                    } ?: "--:--",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Label
                if (alarm?.label?.isNotEmpty() == true) {
                    Text(
                        text = alarm!!.label,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Bouton Stop
                Button(
                    onClick = onStop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = "Stop",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Bouton Snooze (si activé)
                if (alarm?.snoozeEnabled == true) {
                    OutlinedButton(
                        onClick = onSnooze,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Retarder de 5 minutes",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
