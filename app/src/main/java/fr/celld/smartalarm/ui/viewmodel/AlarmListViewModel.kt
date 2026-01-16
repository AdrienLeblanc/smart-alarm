package fr.celld.smartalarm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.celld.smartalarm.data.model.Alarm
import fr.celld.smartalarm.data.repository.AlarmRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel pour la liste des alarmes
 */
class AlarmListViewModel(
    private val repository: AlarmRepository
) : ViewModel() {

    /**
     * Liste des alarmes (StateFlow pour Compose)
     */
    val alarms: StateFlow<List<Alarm>> = repository.getAllAlarms()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Active ou désactive une alarme
     */
    fun toggleAlarm(alarmId: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleAlarm(alarmId, enabled)
            // TODO: Planifier ou annuler l'alarme avec AlarmManager
        }
    }

    /**
     * Supprime une alarme
     */
    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.deleteAlarm(alarm)
            // TODO: Annuler l'alarme planifiée avec AlarmManager
        }
    }

    /**
     * Duplique une alarme
     */
    fun duplicateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val newAlarm = alarm.copy(id = 0, isEnabled = false)
            repository.saveAlarm(newAlarm)
        }
    }
}
