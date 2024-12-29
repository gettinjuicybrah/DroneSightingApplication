package com.example.project.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.service.FirebaseAuthService
import com.example.project.ui.navigation.NavigatorImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
/**
 * ViewModel for handling login functionality in the Android app.
 * Manages user input (username, email, password), authentication via Firebase, and navigation.
 */
class LoginViewModel: ViewModel(), KoinComponent {
    private val navigator: NavigatorImpl by inject()
    // Mutable state flow to hold the current login state
    private val _state = MutableStateFlow(LoginState())
    // Publicly exposed immutable state flow for the UI to observe
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val authService: FirebaseAuthService by inject()
    /**
     * Processes login-related events triggered by the UI.
     * Handles updates to user input, login attempts, navigation, and error dismissal.
     */
    fun handleEvent(event: LoginEvent){
        when (event){
            is LoginEvent.UpdateUsername -> updateUsername(event.username)
            is LoginEvent.UpdateEmail -> updateEmail(event.email)
            is LoginEvent.UpdatePassword -> updatePassword(event.password)
            is LoginEvent.AttemptLogin -> attemptLogin()
            is LoginEvent.Register -> register()
            is LoginEvent.DismissError -> dismissError()
            is LoginEvent.NavigateBack -> navigator.popBackStack()
        }
    }
    /**
     * Updates the username in the login state.
     * @param username The new username value from the UI.
     */
    private fun updateUsername(username: String) {
        _state.value = _state.value.copy(username = username)
    }
    /**
     * Updates the password in the login state.
     * @param password The new password value from the UI.
     */
    private fun updatePassword(password: String) {
        _state.value = _state.value.copy(password = password)
    }
    /**
     * Updates the email in the login state.
     * @param email The new email value from the UI.
     */
    private fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }
    /**
     * Attempts to authenticate the user using the provided email and password.
     * Launches a coroutine to handle the asynchronous Firebase authentication process.
     */
    private fun attemptLogin() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            authService.loginUser(_state.value.email, _state.value.password) {
                success, error ->
                if(success){
                    Log.d("Auth", "User logged in successfully!")
                    navigator.navToSightingsList()
                }
                else {
                    // Handle failure
                    Log.e("Auth", "Login failed: $error")
                }
            }
            _state.value = _state.value.copy(isLoading = false)
            navigator.navToSighting()
        }
    }
    /**
     * Clears any error message from the login state.
     */
    private fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }
    /**
     * Navigates to the registration screen when the user opts to register instead.
     */
    private fun register() {
        navigator.navToRegister()
    }

}
/**
 * Data class representing the state of the login screen.
 * Holds user input and UI-related flags (e.g., loading, error).
 */
data class LoginState(
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
/**
 * Sealed class defining possible events that can occur on the login screen.
 * Used to communicate UI actions to the ViewModel.
 */
sealed class LoginEvent {
    data class UpdateEmail(val email: String) : LoginEvent()
    data class UpdateUsername(val username: String) : LoginEvent()
    data class UpdatePassword(val password: String) : LoginEvent()
    object NavigateBack : LoginEvent()
    object AttemptLogin : LoginEvent()
    object Register : LoginEvent()
    object DismissError : LoginEvent()

}