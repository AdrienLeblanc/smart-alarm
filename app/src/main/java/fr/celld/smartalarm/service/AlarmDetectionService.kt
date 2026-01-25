package fr.celld.smartalarm.service

import android.content.Context
import android.util.Log
import fr.celld.smartalarm.data.model.Alarm
import fr.celld.smartalarm.data.model.DetectionMethod
import fr.celld.smartalarm.service.sensors.MotionMonitoringManager

/**
 * Service to determine if an alarm should be triggered
 * based on its detection method
 */
object AlarmDetectionService {

    /**
     * Determines if an alarm should be triggered
     * @param context Application context
     * @param alarm The alarm to check
     * @return true if the alarm should ring, false otherwise
     */
    fun shouldTriggerAlarm(context: Context, alarm: Alarm): Boolean {
        return when (alarm.detectionMethod) {
            DetectionMethod.ACCELEROMETER -> checkAccelerometerDetection(context)
            DetectionMethod.MANUAL -> true
            else -> {
                Log.w("AlarmDetectionService", "Detection method ${alarm.detectionMethod} not implemented")
                true
            }
        }
    }

    /**
     * Checks if the alarm should ring based on accelerometer detection
     * @return true if the alarm should ring, false if the user seems already awake
     */
    private fun checkAccelerometerDetection(context: Context): Boolean {
        val hasMotion = MotionMonitoringManager.hasMotionInLastMinutes(context, 5)
        return if (hasMotion) {
            Log.d("AlarmDetectionService", "Motion detected in the last 5 minutes, alarm cancelled")
            false
        } else {
            Log.d("AlarmDetectionService", "No motion detected, alarm triggered")
            true
        }
    }
}
