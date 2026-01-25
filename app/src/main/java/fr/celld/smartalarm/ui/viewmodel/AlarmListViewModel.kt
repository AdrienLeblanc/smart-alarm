package fr.celld.smartalarm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.celld.smartalarm.data.model.Alarm
import fr.celld.smartalarm.data.repository.AlarmRepository
import fr.celld.smartalarm.service.AlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel pour la liste des alarmes
 */
class AlarmListViewModel(
    private val repository: AlarmRepository,
    private val scheduler: AlarmScheduler
) : ViewModel() {

    /**
     * List of alarms (StateFlow for Compose)
     */
    val alarms: StateFlow<List<Alarm>> = repository.getAllAlarms()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Enables or disables an alarm
     */
    fun toggleAlarm(alarmId: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleAlarm(alarmId, enabled)
            val alarm = repository.getAlarmById(alarmId)
            alarm?.let {
                if (enabled) {
                    scheduler.scheduleAlarm(it)
                } else {
                    scheduler.cancelAlarm(alarmId)
                }
            }
        }
    }

    /**
     * Deletes an alarm
     */
    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            scheduler.cancelAlarm(alarm.id)
            repository.deleteAlarm(alarm)
        }
    }

    /**
     * Duplicates an alarm
     */
    fun duplicateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val newAlarm = alarm.copy(id = 0, isEnabled = false)
            repository.saveAlarm(newAlarm)
        }
    }
}
