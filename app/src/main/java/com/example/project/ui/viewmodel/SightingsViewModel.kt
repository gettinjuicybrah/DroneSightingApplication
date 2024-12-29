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
/**
 * ViewModel for managing the list of sightings.
 * Handles data loading, navigation, and authentication checks for user actions.
 */
class SightingsViewModel:ViewModel(), KoinComponent {

    private val navigator: NavigatorImpl by inject()
    private val sightingRepository: SightingRepository by inject()
    // State flow for the list of sighting cards displayed in the UI
    private val _state = MutableStateFlow<List<SightingCard>>(emptyList())
    val state: StateFlow<List<SightingCard>> = _state.asStateFlow()

    private val authService: FirebaseAuthService by inject()
    // Define a Channel for one-time events
    private val _event = Channel<UiEvent>(Channel.BUFFERED)
    val eventFlow = _event.receiveAsFlow()
    init {
        // Load sighting data when the ViewModel is initialized
        attemptDataLoad()
    }
    /**
     * Handles events for the sightings screen.
     * Manages navigation and ensures authentication for certain actions.
     */
    fun handleEvent(event: SightingsEvent) {
        when (event) {
            is SightingsEvent.NavigateToNewSighting -> determineAuthThen({ navigateToNewSighting() })
            is SightingsEvent.NavigateToSettings -> navigateToSettings()
            is SightingsEvent.NavigateToSighting -> navigateToSighting()
            is SightingsEvent.NavigateToProfile -> navigateToProfile()
            is SightingsEvent.NavigateToLogin -> navigator.navToLogin()
        }
    }
    /**
     * Loads all sightings from the repository and updates the UI state.
     * Runs in a coroutine to handle asynchronous data fetching.
     */
    private fun attemptDataLoad() {
        viewModelScope.launch {
            sightingRepository.getAll()
                .collect { sightings ->
                    _state.update { sightings.map { sighting -> sighting.toSightingCard() }}
                }
        }
    }
    /**
     * Checks if the user is authenticated before executing an action.
     * If not logged in, triggers a snackbar notification.
     * @param action The action to perform if authenticated.
     * @param callback Optional callback after the action (currently unused).
     */
    private fun determineAuthThen(action: () -> Unit, callback: () -> Unit = {} ) {
        if (authService.isLoggedIn) {
            action()
        } else {
            showNotLoggedInSnackbar()
        }
    }

    /**
     * Emits a snackbar event to notify the user they must log in.
     */
    fun showNotLoggedInSnackbar() {
        viewModelScope.launch {
            _event.send(UiEvent.ShowSnackbar("You must be logged in to do that!", "Login"))
        }
    }

    /**
     * Placeholder function for deleting a sighting (currently unimplemented).
     * @param sightingId The ID of the sighting to delete.
     */
    fun deleteSighting(sightingId: String) {
        viewModelScope.launch {
            // TODO: Implement deletion logic
        }
    }

    /**
     * Extension function to convert a domain Sighting to a UI SightingCard.
     * @return A SightingCard object for display purposes.
     */
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
    /**
     * Sealed class representing one-time UI events.
     */
    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
        data class ShowSnackbar(val message: String, val actionLabel: String) : UiEvent()
    }
}
/**
 * Sealed class defining possible events for the sightings screen.
 */
sealed class SightingsEvent {
    object NavigateToNewSighting: SightingsEvent()
    object NavigateToSighting: SightingsEvent()
    object NavigateToProfile: SightingsEvent()
    object NavigateToSettings: SightingsEvent()
    object NavigateToLogin: SightingsEvent()


}