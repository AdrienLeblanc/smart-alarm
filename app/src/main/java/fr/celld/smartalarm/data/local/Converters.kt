package fr.celld.smartalarm.data.local

import androidx.room.TypeConverter
import fr.celld.smartalarm.data.model.DetectionMethod

/**
 * Convertisseurs pour Room afin de gérer les types non-primitifs
 */
class Converters {

    @TypeConverter
    fun fromIntSet(value: Set<Int>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toIntSet(value: String): Set<Int> {
        return if (value.isEmpty()) {
            emptySet()
        } else {
            value.split(",").map { it.toInt() }.toSet()
        }
    }

    @TypeConverter
    fun fromDetectionMethod(value: DetectionMethod): String {
        return value.name
    }

    @TypeConverter
    fun toDetectionMethod(value: String): DetectionMethod {
        return DetectionMethod.valueOf(value)
    }
}
