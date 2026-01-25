package fr.celld.smartalarm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.celld.smartalarm.data.model.Alarm
import fr.celld.smartalarm.data.model.DetectionMethod
import fr.celld.smartalarm.data.repository.AlarmRepository
import fr.celld.smartalarm.service.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for editing an alarm
 */
class AlarmEditViewModel(
    private val repository: AlarmRepository,
    private val scheduler: AlarmScheduler
) : ViewModel() {

    private val _alarm = MutableStateFlow<Alarm?>(null)
    val alarm: StateFlow<Alarm?> = _alarm.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    /**
     * Loads an existing alarm for editing
     */
    fun loadAlarm(alarmId: Long) {
        viewModelScope.launch {
            _alarm.value = repository.getAlarmById(alarmId)
        }
    }

    /**
     * Creates a new alarm
     */
    fun createNewAlarm() {
        _alarm.value = Alarm(
            hour = 7,
            minute = 0,
            isEnabled = true
        )
    }

    /**
     * Updates the alarm time
     */
    fun updateTime(hour: Int, minute: Int) {
        _alarm.value = _alarm.value?.copy(hour = hour, minute = minute)
    }

    /**
     * Updates the alarm label
     */
    fun updateLabel(label: String) {
        _alarm.value = _alarm.value?.copy(label = label)
    }

    /**
     * Updates repeat days
     */
    fun updateRepeatDays(days: Set<Int>) {
        _alarm.value = _alarm.value?.copy(repeatDays = days)
    }

    /**
     * Toggles a repeat day
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
     * Updates the ringtone
     */
    fun updateRingtone(uri: String) {
        _alarm.value = _alarm.value?.copy(ringtoneUri = uri)
    }

    /**
     * Updates vibration
     */
    fun updateVibrate(vibrate: Boolean) {
        _alarm.value = _alarm.value?.copy(vibrate = vibrate)
    }

    /**
     * Updates the detection method
     */
    fun updateDetectionMethod(method: DetectionMethod) {
        _alarm.value = _alarm.value?.copy(detectionMethod = method)
    }

    /**
     * Updates snooze settings
     */
    fun updateSnooze(enabled: Boolean, duration: Int = 5) {
        _alarm.value = _alarm.value?.copy(
            snoozeEnabled = enabled,
            snoozeDuration = duration
        )
    }

    /**
     * Saves the alarm
     */
    fun saveAlarm(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            _alarm.value?.let { alarm ->
                try {
                    val alarmId = repository.saveAlarm(alarm)
                    val savedAlarm = if (alarm.id == 0L) {
                        alarm.copy(id = alarmId)
                    } else {
                        alarm
                    }
                    if (savedAlarm.isEnabled) {
                        scheduler.scheduleAlarm(savedAlarm)
                    }
                    onSuccess()
                } finally {
                    _isSaving.value = false
                }
            }
        }
    }
}
