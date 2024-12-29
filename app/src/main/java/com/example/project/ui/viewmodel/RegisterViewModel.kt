package com.example.project.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.service.FirebaseAuthService
import com.example.project.ui.navigation.NavigatorImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
/**
 * ViewModel for handling user registration.
 * Manages user input, Firebase registration, and navigation.
 */
class RegisterViewModel: ViewModel(), KoinComponent {
    private val navigator: NavigatorImpl by inject()

    // Mutable state flow for registration state
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    // Firebase authentication service for registration, injected via Koin
    private val authService: FirebaseAuthService by inject()

    /**
     * Handles registration-related events from the UI.
     * Manages input updates, registration attempts, and navigation.
     */
    fun handleEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.UpdateEmail -> updateEmail(event.email)
            is RegisterEvent.UpdateUsername -> updateUsername(event.username)
            is RegisterEvent.UpdatePassword -> updatePassword(event.password)
            is RegisterEvent.AttemptRegister -> attemptRegister()
            is RegisterEvent.GoBack -> goBack()
            is RegisterEvent.DismissError -> dismissError()
        }
    }
    /**
     * Updates the username in the registration state.
     * @param username The new username value from the UI.
     */
    private fun updateUsername(username: String) {
        _state.value = _state.value.copy(username = username)
    }
    /**
     * Updates the password in the registration state.
     * @param password The new password value from the UI.
     */
    private fun updatePassword(password: String) {
        _state.value = _state.value.copy(password = password)
    }
    /**
     * Updates the email in the registration state.
     * @param email The new email value from the UI.
     */
    private fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }
    /**
     * Attempts to register a new user with the provided email and password.
     * Launches a coroutine to handle the asynchronous Firebase registration.
     */
    private fun attemptRegister() {
        viewModelScope.launch {
            // Indicate registration is in progress
            _state.value = _state.value.copy(isLoading = true)

            // Call the authentication service to register the user
            authService.registerUser(_state.value.email, _state.value.password){success, error ->
                if (success) {
                    // Handle successful registration
                    Log.d("Auth", "User registered successfully!")
                    navigator.popBackStack()
                } else {
                    Log.e("Auth", "Registration failed: $error")
                    // TODO: Update state to display error to the user
                }

            }

            _state.value = _state.value.copy(isLoading = false)
            navigator.navToLogin()
        }
    }
    private fun goBack() {
        navigator.popBackStack()
    }
    private fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }
}
/**
 * Data class representing the state of the registration screen.
 * Holds user input and UI-related flags (e.g., loading, error).
 */
data class RegisterState(
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
/**
 * Sealed class defining possible events for the registration screen.
 * Used to communicate UI actions to the ViewModel.
 */
sealed class RegisterEvent {
    data class UpdateEmail(val email: String) : RegisterEvent()
    data class UpdateUsername(val username: String) : RegisterEvent()
    data class UpdatePassword(val password: String) : RegisterEvent()
    object AttemptRegister : RegisterEvent()
    object GoBack : RegisterEvent()
    object DismissError : RegisterEvent()
}