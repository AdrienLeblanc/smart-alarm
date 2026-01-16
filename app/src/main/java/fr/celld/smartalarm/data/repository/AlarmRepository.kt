package fr.celld.smartalarm.data.repository

import fr.celld.smartalarm.data.local.AlarmDao
import fr.celld.smartalarm.data.model.Alarm
import kotlinx.coroutines.flow.Flow

/**
 * Repository pour gérer les opérations sur les alarmes
 */
class AlarmRepository(private val alarmDao: AlarmDao) {

    /**
     * Récupère toutes les alarmes
     */
    fun getAllAlarms(): Flow<List<Alarm>> = alarmDao.getAllAlarms()

    /**
     * Récupère toutes les alarmes activées
     */
    fun getEnabledAlarms(): Flow<List<Alarm>> = alarmDao.getEnabledAlarms()

    /**
     * Récupère une alarme par son ID
     */
    suspend fun getAlarmById(alarmId: Long): Alarm? = alarmDao.getAlarmById(alarmId)

    /**
     * Ajoute ou met à jour une alarme
     */
    suspend fun saveAlarm(alarm: Alarm): Long = alarmDao.insertAlarm(alarm)

    /**
     * Met à jour une alarme
     */
    suspend fun updateAlarm(alarm: Alarm) = alarmDao.updateAlarm(alarm)

    /**
     * Supprime une alarme
     */
    suspend fun deleteAlarm(alarm: Alarm) = alarmDao.deleteAlarm(alarm)

    /**
     * Active/désactive une alarme
     */
    suspend fun toggleAlarm(alarmId: Long, enabled: Boolean) {
        alarmDao.setAlarmEnabled(alarmId, enabled)
    }

    /**
     * Supprime toutes les alarmes
     */
    suspend fun deleteAllAlarms() = alarmDao.deleteAllAlarms()
}
