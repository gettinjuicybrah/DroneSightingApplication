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

class RegisterViewModel: ViewModel(), KoinComponent {
    private val navigator: NavigatorImpl by inject()

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val authService: FirebaseAuthService by inject()

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
    private fun attemptRegister() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            authService.registerUser(_state.value.email, _state.value.password){success, error ->
                if (success) {
                    // Handle successful registration
                    Log.d("Auth", "User registered successfully!")
                } else {
                    // Handle failure
                    Log.e("Auth", "Registration failed: $error")
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

data class RegisterState(
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class RegisterEvent {
    data class UpdateEmail(val email: String) : RegisterEvent()
    data class UpdateUsername(val username: String) : RegisterEvent()
    data class UpdatePassword(val password: String) : RegisterEvent()
    object AttemptRegister : RegisterEvent()
    object GoBack : RegisterEvent()
    object DismissError : RegisterEvent()
}