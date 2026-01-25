package fr.celld.smartalarm.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import fr.celld.smartalarm.data.model.Alarm
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for alarms
 */
@Dao
interface AlarmDao {

    /**
     * Retrieves all alarms (observable flow)
     */
    @Query("SELECT * FROM alarms ORDER BY hour, minute")
    fun getAllAlarms(): Flow<List<Alarm>>

    /**
     * Retrieves all enabled alarms
     */
    @Query("SELECT * FROM alarms WHERE isEnabled = 1 ORDER BY hour, minute")
    fun getEnabledAlarms(): Flow<List<Alarm>>

    /**
     * Retrieves an alarm by its ID
     */
    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    suspend fun getAlarmById(alarmId: Long): Alarm?

    /**
     * Inserts a new alarm
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: Alarm): Long

    /**
     * Updates an existing alarm
     */
    @Update
    suspend fun updateAlarm(alarm: Alarm)

    /**
     * Deletes an alarm
     */
    @Delete
    suspend fun deleteAlarm(alarm: Alarm)

    /**
     * Deletes all alarms
     */
    @Query("DELETE FROM alarms")
    suspend fun deleteAllAlarms()

    /**
     * Enables/disables an alarm
     */
    @Query("UPDATE alarms SET isEnabled = :enabled WHERE id = :alarmId")
    suspend fun setAlarmEnabled(alarmId: Long, enabled: Boolean)
}
