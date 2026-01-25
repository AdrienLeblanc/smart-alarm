package fr.celld.smartalarm.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import fr.celld.smartalarm.data.model.Alarm
import fr.celld.smartalarm.service.sensors.MotionMonitoringManager
import java.util.Calendar

/**
 * Service to schedule and manage alarms with AlarmManager
 */
class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val ALARM_ID_EXTRA = "alarm_id"
        const val ACTION_ALARM_TRIGGER = "ALARM_TRIGGER"
    }

    /**
     * Schedules an alarm
     */
    fun scheduleAlarm(alarm: Alarm) {
        if (!alarm.isEnabled) return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_TRIGGER
            putExtra(ALARM_ID_EXTRA, alarm.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = getNextAlarmTime(alarm)

        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            calendar.timeInMillis,
            null
        )

        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)

        // Update motion monitoring state
        MotionMonitoringManager.updateMonitoringState(context)
    }

    /**
     * Cancels a scheduled alarm
     */
    fun cancelAlarm(alarmId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_TRIGGER
            putExtra(ALARM_ID_EXTRA, alarmId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Log.d("AlarmScheduler", "Alarm cancelled: $alarmId")

        // Update motion monitoring state
        MotionMonitoringManager.updateMonitoringState(context)
    }

    /**
     * Schedules an alarm in snooze mode (repeat after X minutes)
     */
    fun scheduleSnooze(alarmId: Long, snoozeDurationMinutes: Int) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_TRIGGER
            putExtra(ALARM_ID_EXTRA, alarmId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + (snoozeDurationMinutes * 60 * 1000)

        // Use setAlarmClock so snooze rings even in Do Not Disturb mode
        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            triggerTime,
            null  // No show intent
        )

        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    /**
     * Calculates the next occurrence of the alarm
     */
    private fun getNextAlarmTime(alarm: Alarm): Calendar {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Handle repeats
        if (alarm.repeatDays.isNotEmpty()) {
            // If the time hasn't passed today yet, check if today is in repeatDays
            val isPastTime = calendar.timeInMillis <= System.currentTimeMillis()

            var daysToAdd = 0
            var currentDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7

            // If time has passed, start searching from tomorrow
            if (isPastTime) {
                daysToAdd = 1
                currentDayOfWeek = (currentDayOfWeek + 1) % 7
            }

            // Find the next repeat day
            for (unused in 0..7) {
                if (alarm.repeatDays.contains(currentDayOfWeek)) {
                    break
                }
                daysToAdd++
                currentDayOfWeek = (currentDayOfWeek + 1) % 7
            }

            calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
        } else {
            // One-time alarm: if time has passed, schedule for tomorrow
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        return calendar
    }

}
