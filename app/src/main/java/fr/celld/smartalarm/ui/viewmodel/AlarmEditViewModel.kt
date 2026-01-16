package fr.celld.smartalarm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.celld.smartalarm.data.model.Alarm
import fr.celld.smartalarm.data.model.DetectionMethod
import fr.celld.smartalarm.data.repository.AlarmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel pour l'édition d'une alarme
 */
class AlarmEditViewModel(
    private val repository: AlarmRepository
) : ViewModel() {

    private val _alarm = MutableStateFlow<Alarm?>(null)
    val alarm: StateFlow<Alarm?> = _alarm.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    /**
     * Charge une alarme existante pour l'édition
     */
    fun loadAlarm(alarmId: Long) {
        viewModelScope.launch {
            _alarm.value = repository.getAlarmById(alarmId)
        }
    }

    /**
     * Crée une nouvelle alarme
     */
    fun createNewAlarm() {
        _alarm.value = Alarm(
            hour = 7,
            minute = 0,
            isEnabled = true
        )
    }

    /**
     * Met à jour l'heure de l'alarme
     */
    fun updateTime(hour: Int, minute: Int) {
        _alarm.value = _alarm.value?.copy(hour = hour, minute = minute)
    }

    /**
     * Met à jour le label de l'alarme
     */
    fun updateLabel(label: String) {
        _alarm.value = _alarm.value?.copy(label = label)
    }

    /**
     * Met à jour les jours de répétition
     */
    fun updateRepeatDays(days: Set<Int>) {
        _alarm.value = _alarm.value?.copy(repeatDays = days)
    }

    /**
     * Toggle un jour de répétition
     */
    fun toggleRepeatDay(day: Int) {
        _alarm.value?.let { currentAlarm ->
            val newDays = if (currentAlarm.repeatDays.contains(day)) {
                currentAlarm.repeatDays - day
            } else {
                currentAlarm.repeatDays + day
            }
            _alarm.value = currentAlarm.copy(repeatDays = newDays)
        }
    }

    /**
     * Met à jour la sonnerie
     */
    fun updateRingtone(uri: String) {
        _alarm.value = _alarm.value?.copy(ringtoneUri = uri)
    }

    /**
     * Met à jour la vibration
     */
    fun updateVibrate(vibrate: Boolean) {
        _alarm.value = _alarm.value?.copy(vibrate = vibrate)
    }

    /**
     * Met à jour la méthode de détection
     */
    fun updateDetectionMethod(method: DetectionMethod) {
        _alarm.value = _alarm.value?.copy(detectionMethod = method)
    }

    /**
     * Met à jour le snooze
     */
    fun updateSnooze(enabled: Boolean, duration: Int = 5) {
        _alarm.value = _alarm.value?.copy(
            snoozeEnabled = enabled,
            snoozeDuration = duration
        )
    }

    /**
     * Enregistre l'alarme
     */
    fun saveAlarm(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            _alarm.value?.let { alarm ->
                try {
                    repository.saveAlarm(alarm)
                    // TODO: Planifier l'alarme avec AlarmManager
                    onSuccess()
                } finally {
                    _isSaving.value = false
                }
            }
        }
    }
}
