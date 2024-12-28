package com.example.project.ui.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.model.domain.Sighting
import com.example.project.data.model.ui.SightingCard
import com.example.project.data.repository.SightingRepository
import com.example.project.service.FirebaseAuthService
import com.example.project.ui.navigation.NavigatorImpl
import kotlinx.coroutines.channels.Channel

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SightingsViewModel:ViewModel(), KoinComponent {

    private val navigator: NavigatorImpl by inject()
    private val sightingRepository: SightingRepository by inject()

    private val _state = MutableStateFlow<List<SightingCard>>(emptyList())
    val state: StateFlow<List<SightingCard>> = _state.asStateFlow()

    private val authService: FirebaseAuthService by inject()
    // Define a Channel for one-time events
    private val _event = Channel<UiEvent>(Channel.BUFFERED)
    val eventFlow = _event.receiveAsFlow()
    init {
        attemptDataLoad()
    }

    fun handleEvent(event: SightingsEvent) {
        when (event) {
            is SightingsEvent.NavigateToNewSighting -> determineAuthThen({ navigateToNewSighting() })
            is SightingsEvent.NavigateToSettings -> navigateToSettings()
            is SightingsEvent.NavigateToSighting -> navigateToSighting()
            is SightingsEvent.NavigateToProfile -> navigateToProfile()
            is SightingsEvent.NavigateToLogin -> navigator.navToLogin()
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

    private fun determineAuthThen(action: () -> Unit, callback: () -> Unit = {} ) {
        if (authService.isLoggedIn) {
            action()
        } else {
            showNotLoggedInSnackbar()
        }
    }

    // Function to emit a snackbar event
    fun showNotLoggedInSnackbar() {
        viewModelScope.launch {
            _event.send(UiEvent.ShowSnackbar("You must be logged in to do that!", "Login"))
        }
    }

    private fun notLoggedIn():String{
        return "You must be logged in to do that!"
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
    // UI Event class to represent different events
    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowSnackbar(val message: String, val actionLabel: String) : UiEvent()
    }
}
sealed class SightingsEvent {
    object NavigateToNewSighting: SightingsEvent()
    object NavigateToSighting: SightingsEvent()
    object NavigateToProfile: SightingsEvent()
    object NavigateToSettings: SightingsEvent()
    object NavigateToLogin: SightingsEvent()


}