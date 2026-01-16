package fr.celld.smartalarm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * BroadcastReceiver pour recevoir les alarmes planifiées
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(AlarmScheduler.ALARM_ID_EXTRA, -1)

        if (alarmId == -1L) {
            Log.e("AlarmReceiver", "Invalid alarm ID")
            return
        }

        Log.d("AlarmReceiver", "Alarm received: $alarmId")

        // TODO: Démarrer l'activité de l'alarme qui sonne
        // TODO: Vérifier le capteur de détection si configuré
        // TODO: Jouer la sonnerie et vibrer

        // Pour l'instant, on lance juste un service ou une notification
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra(AlarmScheduler.ALARM_ID_EXTRA, alarmId)
        }

        context.startForegroundService(serviceIntent)
    }
}
