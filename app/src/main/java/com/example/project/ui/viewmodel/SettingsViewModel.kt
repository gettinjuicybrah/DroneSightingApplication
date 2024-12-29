package com.example.project.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.project.data.model.ui.SightingCard
import com.example.project.data.repository.SightingRepository
import com.example.project.ui.navigation.NavigatorImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
/**
 * ViewModel for the settings screen.
 * Manages navigation and holds a state for sighting cards (currently unused).
 */
class SettingsViewModel: ViewModel(), KoinComponent {
    private val navigator: NavigatorImpl by inject()
    // State flow for a list of sighting cards (placeholder for future use)
    private val _state = MutableStateFlow<List<SightingCard>>(emptyList())
    val state: StateFlow<List<SightingCard>> = _state.asStateFlow()
    /**
     * Handles events for the settings screen.
     * Currently only supports navigating back.
     */
    fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.NavigateBack -> navigator.popBackStack()
        }
    }
    /**
     * Sealed class defining possible events for the settings screen.
     */
    sealed class SettingsEvent {
        object NavigateBack: SettingsEvent()
    }
}