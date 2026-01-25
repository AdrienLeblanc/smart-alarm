package fr.celld.smartalarm.service.sensors

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Build
import fr.celld.smartalarm.data.local.AlarmDatabase
import fr.celld.smartalarm.data.model.DetectionMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Manager for motion monitoring
 * Starts or stops the monitoring service based on configured alarms
 */
object MotionMonitoringManager {

    private const val PREFS_NAME = "motion_detection_prefs"
    private const val KEY_LAST_MOTION_TIME = "last_motion_time"

    /**
     * Starts the motion monitoring service
     */
    fun startMonitoring(context: Context) {
        val intent = Intent(context, AccelerometerMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    /**
     * Stops the motion monitoring service
     */
    fun stopMonitoring(context: Context) {
        val intent = Intent(context, AccelerometerMonitorService::class.java)
        context.stopService(intent)
    }

    /**
     * Checks if motion was detected in the last X minutes
     * @param context Application context
     * @param minutesBefore Number of minutes to check
     * @return true if motion was detected, false otherwise
     */
    fun hasMotionInLastMinutes(context: Context, minutesBefore: Int): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val lastMotionTime = prefs.getLong(KEY_LAST_MOTION_TIME, 0L)
        val currentTime = System.currentTimeMillis()
        val thresholdTime = minutesBefore * 60 * 1000L // Convert to milliseconds

        return (currentTime - lastMotionTime) <= thresholdTime
    }

    /**
     * Checks if active alarms use accelerometer detection
     * and starts/stops the monitoring service accordingly
     */
    fun updateMonitoringState(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = AlarmDatabase.Companion.getDatabase(context)
            val dao = database.alarmDao()

            // Get all alarms from Flow
            val alarmsList = dao.getAllAlarms().first()

            // Check if at least one active alarm uses accelerometer
            val needsMonitoring = alarmsList.any { alarm ->
                alarm.isEnabled && alarm.detectionMethod == DetectionMethod.ACCELEROMETER
            }

            if (needsMonitoring) {
                // Start monitoring service
                startMonitoring(context)
            } else {
                // Stop monitoring service
                stopMonitoring(context)
            }
        }
    }
}