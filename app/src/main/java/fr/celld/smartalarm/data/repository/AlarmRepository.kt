package fr.celld.smartalarm.data.repository

import fr.celld.smartalarm.data.local.AlarmDao
import fr.celld.smartalarm.data.model.Alarm
import kotlinx.coroutines.flow.Flow

/**
 * Repository to manage alarm operations
 */
class AlarmRepository(private val alarmDao: AlarmDao) {

    /**
     * Retrieves all alarms
     */
    fun getAllAlarms(): Flow<List<Alarm>> = alarmDao.getAllAlarms()

    /**
     * Retrieves all enabled alarms
     */
    fun getEnabledAlarms(): Flow<List<Alarm>> = alarmDao.getEnabledAlarms()

    /**
     * Retrieves an alarm by its ID
     */
    suspend fun getAlarmById(alarmId: Long): Alarm? = alarmDao.getAlarmById(alarmId)

    /**
     * Adds or updates an alarm
     */
    suspend fun saveAlarm(alarm: Alarm): Long = alarmDao.insertAlarm(alarm)

    /**
     * Updates an alarm
     */
    suspend fun updateAlarm(alarm: Alarm) = alarmDao.updateAlarm(alarm)

    /**
     * Deletes an alarm
     */
    suspend fun deleteAlarm(alarm: Alarm) = alarmDao.deleteAlarm(alarm)

    /**
     * Enables/disables an alarm
     */
    suspend fun toggleAlarm(alarmId: Long, enabled: Boolean) {
        alarmDao.setAlarmEnabled(alarmId, enabled)
    }

    /**
     * Deletes all alarms
     */
    suspend fun deleteAllAlarms() = alarmDao.deleteAllAlarms()
}
