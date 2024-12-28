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

class ProfileViewModel:ViewModel(), KoinComponent {
    private val navigator: NavigatorImpl by inject()

    private val _state = MutableStateFlow<List<SightingCard>>(emptyList())
    val state: StateFlow<List<SightingCard>> = _state.asStateFlow()

    fun handleEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.NavigateBack -> navigator.popBackStack()
        }

    }

    sealed class ProfileEvent {
        object NavigateBack: ProfileEvent()
    }
}