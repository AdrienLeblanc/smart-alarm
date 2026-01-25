package fr.celld.smartalarm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import fr.celld.smartalarm.data.local.AlarmDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver to receive scheduled alarms
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(AlarmScheduler.ALARM_ID_EXTRA, -1)

        if (alarmId == -1L) {
            Log.e("AlarmReceiver", "❌ Invalid alarm ID")
            return
        }

        Log.d("AlarmReceiver", "✅ Alarm received: $alarmId")

        // Check detection sensor before triggering the alarm
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AlarmDatabase.getDatabase(context)
                val alarm = database.alarmDao().getAlarmById(alarmId)

                if (alarm == null) {
                    Log.e("AlarmReceiver", "Alarm not found: $alarmId")
                    pendingResult.finish()
                    return@launch
                }

                // Check if the alarm should be triggered
                val shouldTriggerAlarm = AlarmDetectionService.shouldTriggerAlarm(context, alarm)

                if (shouldTriggerAlarm) {
                    // Start the alarm service
                    val serviceIntent = Intent(context, AlarmService::class.java).apply {
                        putExtra(AlarmScheduler.ALARM_ID_EXTRA, alarmId)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                } else {
                    Log.i("AlarmReceiver", "Alarm $alarmId skipped because user seems already awake")
                }

            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Erreur lors de la vérification de l'alarme", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
