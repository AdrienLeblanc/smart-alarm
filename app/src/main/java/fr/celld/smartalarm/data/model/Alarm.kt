package fr.celld.smartalarm.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import fr.celld.smartalarm.data.local.Converters
import java.time.LocalTime

/**
 * Entity representing an alarm in the database
 */
@Entity(tableName = "alarms")
@TypeConverters(Converters::class)
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val hour: Int,
    val minute: Int,

    val isEnabled: Boolean = true,

    val label: String = "",

    // Repeat days (Monday = 0, Sunday = 6)
    val repeatDays: Set<Int> = emptySet(),

    val ringtoneUri: String = "",

    val vibrate: Boolean = true,

    val detectionMethod: DetectionMethod = DetectionMethod.MANUAL,

    val snoozeEnabled: Boolean = true,
    val snoozeDuration: Int = 5 // minutes
) {
    /**
     * Returns the alarm time
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(): LocalTime = LocalTime.of(hour, minute)

    /**
     * Checks if the alarm is scheduled for a given day
     * @param dayOfWeek Day of the week (0 = Monday, 6 = Sunday)
     */
    fun isScheduledForDay(dayOfWeek: Int): Boolean {
        return repeatDays.isEmpty() || repeatDays.contains(dayOfWeek)
    }

    /**
     * Checks if the alarm repeats
     */
    fun isRepeating(): Boolean = repeatDays.isNotEmpty()
}

/**
 * Detection method to determine if the user is already awake
 */
enum class DetectionMethod {
    MANUAL,              // Manual deactivation only
    MOTION_SENSOR,       // Motion sensor
    LIGHT_SENSOR,        // Light sensor
    SOUND_SENSOR,        // Sound sensor
    ACCELEROMETER        // Accelerometer
}
