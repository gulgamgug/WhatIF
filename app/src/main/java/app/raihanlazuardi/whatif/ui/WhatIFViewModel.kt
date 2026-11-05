package app.raihanlazuardi.whatif.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.raihanlazuardi.whatif.data.model.FastingSchedule
import app.raihanlazuardi.whatif.data.model.MokelRecord
import app.raihanlazuardi.whatif.data.model.UserProfile
import app.raihanlazuardi.whatif.data.model.WhatIFData
import app.raihanlazuardi.whatif.data.notification.FastingScheduler
import app.raihanlazuardi.whatif.data.notification.NotificationHelper
import app.raihanlazuardi.whatif.data.storage.WhatIFRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class WhatIFViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WhatIFRepository.getInstance(application)

    val uiState: StateFlow<WhatIFData> = repository.dataFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WhatIFData()
        )

    init {
        viewModelScope.launch {
            repository.checkAndAutoCompleteToday(repository.dataFlow.value.schedule)
        }
    }

    fun checkAndAutoCompleteFasting() {
        viewModelScope.launch {
            repository.checkAndAutoCompleteToday(uiState.value.schedule)
        }
    }

    fun updateSchedule(schedule: FastingSchedule) {
        viewModelScope.launch {
            repository.updateSchedule(schedule)
            if (uiState.value.profile.notificationsEnabled) {
                FastingScheduler.scheduleAlarms(getApplication(), schedule)
            }
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        val currentProfile = uiState.value.profile
        updateProfile(currentProfile.copy(notificationsEnabled = enabled))
        if (enabled) {
            FastingScheduler.scheduleAlarms(getApplication(), uiState.value.schedule)
        } else {
            FastingScheduler.cancelAlarms(getApplication())
        }
    }

    fun sendTestNotification() {
        NotificationHelper.showNotification(
            context = getApplication(),
            title = "Tes Notifikasi WhatIF 🥗",
            message = "Notifikasi pengingat puasa & jendela makan berfungsi dengan baik!",
            notificationId = NotificationHelper.NOTIFICATION_ID_TEST
        )
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.updateProfile(profile)
        }
    }

    fun updateProfileName(name: String) {
        val currentProfile = uiState.value.profile
        updateProfile(currentProfile.copy(name = name))
    }

    fun addMokel(food: String, note: String = "", dateTime: java.time.LocalDateTime = java.time.LocalDateTime.now()) {
        val record = MokelRecord(
            foodDescription = food,
            timestamp = dateTime,
            note = note
        )
        viewModelScope.launch {
            repository.addMokel(record)
        }
    }

    fun deleteMokel(id: String) {
        viewModelScope.launch {
            repository.deleteMokel(id)
        }
    }

    fun toggleFastSuccess(date: LocalDate) {
        viewModelScope.launch {
            repository.toggleFastSuccess(date)
        }
    }

    fun exportDatabaseJson(): String {
        return repository.exportToJson()
    }

    fun importDatabaseJson(jsonString: String): Result<Unit> {
        return repository.importFromJson(jsonString)
    }

    fun resetDatabase() {
        viewModelScope.launch {
            repository.resetData()
        }
    }

    fun getMokelsForDate(date: LocalDate): List<MokelRecord> {
        return repository.getMokelsForDate(date)
    }
}
