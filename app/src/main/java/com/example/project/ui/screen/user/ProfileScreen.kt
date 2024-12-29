package com.example.project.ui.screen.user

import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.project.ui.screen.sightings.SightingCard
import com.example.project.ui.viewmodel.ProfileViewModel
import com.example.project.ui.viewmodel.SightingsEvent
import com.example.project.ui.viewmodel.SightingsViewModel
import org.koin.compose.viewmodel.koinViewModel
// This composable function defines the UI for the Profile screen of the app.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val viewModel: ProfileViewModel = koinViewModel()
    //The state we expose to UI. Read-Only
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleEvent(ProfileViewModel.ProfileEvent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Pop backstack")
                    }
                }
            )
        }){ padding ->
        // LazyColumn displays a scrollable list of profile information
        // Currently a placeholder with no items
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(0) { info ->
                Text(text = "Profile information") // Placeholder text for future profile items
            }
        }

    }
}