package com.example.project.ui.viewmodel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.model.domain.Sighting
import com.example.project.data.model.ui.SightingCard
import com.example.project.data.repository.FirestoreRepository
import com.example.project.data.repository.SightingRepository
import com.example.project.ui.navigation.NavigatorImpl

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SightingsViewModel:ViewModel(), KoinComponent {

    private val navigator: NavigatorImpl by inject()
    private val sightingRepository: SightingRepository by inject()

    private val _state = MutableStateFlow<List<SightingCard>>(emptyList())
    val state: StateFlow<List<SightingCard>> = _state.asStateFlow()

    init {
        attemptDataLoad()
    }

    fun handleEvent(event: SightingsEvent) {
        when (event) {
            is SightingsEvent.NavigateToNewSighting -> navigateToNewSighting()
            is SightingsEvent.NavigateToSettings -> navigateToSettings()
            is SightingsEvent.NavigateToSighting -> navigateToSighting()
            is SightingsEvent.NavigateToProfile -> navigateToProfile()
        }
    }

    private fun attemptDataLoad() {
        viewModelScope.launch {
            sightingRepository.getAll()
                .collect { sightings ->
                    _state.update { sightings.map { sighting -> sighting.toSightingCard() }}
                }
        }
    }

    fun deleteSighting(sightingId: String) {
        viewModelScope.launch {

        }
    }

    fun Sighting.toSightingCard(): SightingCard {
        return SightingCard(
            sightingId = sightingId,
            userId = userId,
            username = username,
            location = location,
            postDate = postDate,
            sightingDate = sightingDate,
            description = description,
            mediaUrls = mediaUrls,
            title = title,
            upvotes = upvotes,
            downvotes = downvotes
        )
    }

    private fun navigateToProfile(){
        navigator.navToProfile()
    }

    private fun navigateToNewSighting(){
        navigator.navToNewSighting()
    }

    private fun navigateToSettings(){
        navigator.navToSettings()
    }

    private fun navigateToSighting(){
        navigator.navToSighting()
    }

}
sealed class SightingsEvent {
    object NavigateToNewSighting: SightingsEvent()
    object NavigateToSighting: SightingsEvent()
    object NavigateToProfile: SightingsEvent()
    object NavigateToSettings: SightingsEvent()

}