package com.example.project.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.project.ui.viewmodel.RegisterEvent
import com.example.project.ui.viewmodel.RegisterViewModel
import org.koin.compose.viewmodel.koinViewModel
// This composable function defines the UI for the Register screen of the app.
@Composable
fun RegisterScreen(){
    val viewModel: RegisterViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = state.email,
            onValueChange = { viewModel.handleEvent(RegisterEvent.UpdateEmail(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = state.password,
            onValueChange = { viewModel.handleEvent(RegisterEvent.UpdatePassword(it)) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { viewModel.handleEvent(RegisterEvent.AttemptRegister) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Register")
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { viewModel.handleEvent(RegisterEvent.GoBack) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text("Back to Login")
        }
    }

    // Error popup
    if (state.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.handleEvent(RegisterEvent.DismissError) },
            title = { Text("Error") },
            text = { Text(state.error!!) },
            confirmButton = {
                Button(onClick = { viewModel.handleEvent(RegisterEvent.DismissError) }) {
                    Text("OK")
                }
            }
        )
    }
}