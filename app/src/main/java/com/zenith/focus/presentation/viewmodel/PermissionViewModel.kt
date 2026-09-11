package com.zenith.focus.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zenith.focus.data.repository.SettingsRepository
import com.zenith.focus.domain.usecase.PermissionCheckUseCase
import com.zenith.focus.data.model.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PermissionViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)
    private val permissionCheckUseCase = PermissionCheckUseCase(application)

    private val _permissionStatus = MutableStateFlow(PermissionStatus())
    val permissionStatus: StateFlow<PermissionStatus> = _permissionStatus.asStateFlow()

    init {
        refreshPermissionStatus()
    }

    fun refreshPermissionStatus() {
        viewModelScope.launch {
            val status = PermissionStatus(
                notificationAccess = permissionCheckUseCase.hasNotificationAccessPermission(),
                screenOverlay = permissionCheckUseCase.hasScreenOverlayPermission(),
                batteryOptimization = permissionCheckUseCase.hasIgnoreBatteryOptimizationPermission(),
                backgroundExecution = true // Will be checked via job scheduler
            )
            _permissionStatus.value = status
            settingsRepository.updatePermissionStatus(status)
        }
    }

    fun requestNotificationAccess() {
        permissionCheckUseCase.requestNotificationAccess()
    }

    fun requestScreenOverlayPermission() {
        permissionCheckUseCase.requestScreenOverlayPermission()
    }

    fun requestIgnoreBatteryOptimization() {
        permissionCheckUseCase.requestIgnoreBatteryOptimization()
    }
}
