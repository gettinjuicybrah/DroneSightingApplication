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

class LoginViewModel: ViewModel(), KoinComponent {
    private val navigator: NavigatorImpl by inject()
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val authService: FirebaseAuthService by inject()

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
    private fun updateUsername(username: String) {
        _state.value = _state.value.copy(username = username)
    }

    private fun updatePassword(password: String) {
        _state.value = _state.value.copy(password = password)
    }
    private fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }
    //assuming success
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
    private fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }
    private fun register() {
        navigator.navToRegister()
    }

}

data class LoginState(
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class LoginEvent {
    data class UpdateEmail(val email: String) : LoginEvent()
    data class UpdateUsername(val username: String) : LoginEvent()
    data class UpdatePassword(val password: String) : LoginEvent()
    object NavigateBack : LoginEvent()
    object AttemptLogin : LoginEvent()
    object Register : LoginEvent()
    object DismissError : LoginEvent()

}