package com.example.project.ui.screen.sightings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.data.model.ui.SightingCard
import com.example.project.ui.viewmodel.SightingsEvent
import com.example.project.ui.viewmodel.SightingsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun SightingListScreen() {
    val viewModel: SightingsViewModel = koinViewModel()
    //The state we expose to UI. Read-Only
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {

            TopAppBar(
                title = { Text("REPORTED SIGHTINGS") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleEvent(SightingsEvent.NavigateToSettings) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }}, actions = {
                    IconButton(onClick = { viewModel.handleEvent(SightingsEvent.NavigateToProfile) }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.handleEvent(SightingsEvent.NavigateToNewSighting)  }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Report Drone Sighting",
                )
            }
        },

        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(Icons.Filled.Home, contentDescription = "Drone Sightings List")
                    }
/*
                    IconButton(onClick = { viewModel.handleEvent(SightingsEvent.NavigateToNewSighting) }) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Report Drone Sighting",
                        )
                    }

 */
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(state) { sighting ->
                SightingCard(sighting)
            }
        }
    }
}

@Composable
fun SightingCard(sighting: SightingCard) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        //elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: User and metadata
            Text(text = "location: ${sighting.location.toString()} - postdate: ${sighting.postDate.toString()}")
            Text(text = "title: ${sighting.title}", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "username: ${sighting.username} - sighting date:${sighting.sightingDate} Hours")


            if (sighting.mediaUrls.isNullOrEmpty()) {
                sighting.description?.take(80)?.let { Text(text = it) }
            } else {
                //implement slideshow scrolling of attached media.
                /*
                sighting.images?.let { images ->
                    LazyRow {
                        items(images) { image ->
                            AsyncImage(
                                model = image.url,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(120.dp)
                                    .padding(4.dp)
                            )
                        }
                    }
                    */

            }
            // Footer: Comments and actions
            Text(text = "Comment amnt: ${sighting.commentCount}")
        }

    }
}
