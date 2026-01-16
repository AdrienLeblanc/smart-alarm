package fr.celld.smartalarm.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import fr.celld.smartalarm.data.model.Alarm
import java.util.Calendar

/**
 * Service pour planifier et gérer les alarmes avec AlarmManager
 */
class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Planifie une alarme
     */
    fun scheduleAlarm(alarm: Alarm) {
        if (!alarm.isEnabled) return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(ALARM_ID_EXTRA, alarm.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = getNextAlarmTime(alarm)

        // Utilise setAlarmClock pour que l'alarme sonne même en mode Ne pas déranger
        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            calendar.timeInMillis,
            pendingIntent
        )

        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    /**
     * Annule une alarme planifiée
     */
    fun cancelAlarm(alarmId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    /**
     * Calcule la prochaine occurrence de l'alarme
     */
    private fun getNextAlarmTime(alarm: Alarm): Calendar {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Si l'heure est déjà passée aujourd'hui
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Gestion des répétitions
        if (alarm.repeatDays.isNotEmpty()) {
            // Trouver le prochain jour de répétition
            var daysToAdd = 0
            var currentDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Convertir de dimanche=1 à lundi=0

            for (i in 0..7) {
                if (alarm.repeatDays.contains(currentDayOfWeek)) {
                    break
                }
                daysToAdd++
                currentDayOfWeek = (currentDayOfWeek + 1) % 7
            }

            if (daysToAdd > 0) {
                calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
            }
        }

        return calendar
    }

    companion object {
        const val ALARM_ID_EXTRA = "alarm_id"
    }
}
