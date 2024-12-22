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
import androidx.compose.ui.Modifier
import com.example.project.ui.screen.sightings.SightingCard
import com.example.project.ui.viewmodel.SightingsEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Pop backstack")
                    }
                }
            )
        }){ padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(0) { info ->
                Text(text = "Profile information")
            }
        }

    }
}