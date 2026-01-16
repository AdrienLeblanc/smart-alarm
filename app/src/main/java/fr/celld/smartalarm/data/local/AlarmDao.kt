package fr.celld.smartalarm.data.local

import androidx.room.*
import fr.celld.smartalarm.data.model.Alarm
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object pour les alarmes
 */
@Dao
interface AlarmDao {

    /**
     * Récupère toutes les alarmes (flux observable)
     */
    @Query("SELECT * FROM alarms ORDER BY hour, minute")
    fun getAllAlarms(): Flow<List<Alarm>>

    /**
     * Récupère toutes les alarmes activées
     */
    @Query("SELECT * FROM alarms WHERE isEnabled = 1 ORDER BY hour, minute")
    fun getEnabledAlarms(): Flow<List<Alarm>>

    /**
     * Récupère une alarme par son ID
     */
    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    suspend fun getAlarmById(alarmId: Long): Alarm?

    /**
     * Insert une nouvelle alarme
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: Alarm): Long

    /**
     * Met à jour une alarme existante
     */
    @Update
    suspend fun updateAlarm(alarm: Alarm)

    /**
     * Supprime une alarme
     */
    @Delete
    suspend fun deleteAlarm(alarm: Alarm)

    /**
     * Supprime toutes les alarmes
     */
    @Query("DELETE FROM alarms")
    suspend fun deleteAllAlarms()

    /**
     * Active/désactive une alarme
     */
    @Query("UPDATE alarms SET isEnabled = :enabled WHERE id = :alarmId")
    suspend fun setAlarmEnabled(alarmId: Long, enabled: Boolean)
}
