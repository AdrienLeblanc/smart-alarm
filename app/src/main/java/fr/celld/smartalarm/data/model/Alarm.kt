package fr.celld.smartalarm.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import fr.celld.smartalarm.data.local.Converters
import java.time.LocalTime

/**
 * Entité représentant une alarme dans la base de données
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

    // Jours de répétition (lundi = 0, dimanche = 6)
    val repeatDays: Set<Int> = emptySet(),

    val ringtoneUri: String = "",

    val vibrate: Boolean = true,

    val detectionMethod: DetectionMethod = DetectionMethod.MANUAL,

    val snoozeEnabled: Boolean = true,
    val snoozeDuration: Int = 5 // minutes
) {
    /**
     * Retourne l'heure de l'alarme
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(): LocalTime = LocalTime.of(hour, minute)

    /**
     * Vérifie si l'alarme est programmée pour un jour donné
     * @param dayOfWeek Jour de la semaine (0 = lundi, 6 = dimanche)
     */
    fun isScheduledForDay(dayOfWeek: Int): Boolean {
        return repeatDays.isEmpty() || repeatDays.contains(dayOfWeek)
    }

    /**
     * Vérifie si l'alarme se répète
     */
    fun isRepeating(): Boolean = repeatDays.isNotEmpty()
}

/**
 * Méthode de détection pour savoir si l'utilisateur est déjà réveillé
 */
enum class DetectionMethod {
    MANUAL,              // Désactivation manuelle uniquement
    MOTION_SENSOR,       // Capteur de mouvement
    LIGHT_SENSOR,        // Capteur de luminosité
    SOUND_SENSOR,        // Capteur de son
    ACCELEROMETER        // Accéléromètre
}
