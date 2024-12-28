package com.example.project.ui.screen.sightings

import android.media.browse.MediaBrowser
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.Uri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
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
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    // Listen for events in the way of lifecycle effect
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SightingsViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is SightingsViewModel.UiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        // Perform navigation when the action is clicked
                        viewModel.handleEvent(SightingsEvent.NavigateToLogin)
                    }
                }
            }
        }
    }
    Scaffold(
        snackbarHost = {  SnackbarHost(snackbarHostState) },
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

                sighting.mediaUrls?.let { mediaUrls ->

                    val context = LocalContext.current
                    LazyRow {
                        items(mediaUrls) { mediaUrl ->

                            if (mediaUrl.endsWith(".mp4") || mediaUrl.contains("video")) {
                                VideoPlayer(url = mediaUrl)
                            } else {

                                val imageRequest = remember {
                                    ImageRequest.Builder(context)
                                        .data(mediaUrl)
                                        .crossfade(true)
                                        .build()
                                }
                                AsyncImage(
                                    model = imageRequest,
                                    contentDescription = null,
                                    modifier = Modifier.size(128.dp),
                                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                                    error = painterResource(id = android.R.drawable.stat_notify_error)
                                )
                            }
                            }
                    }

                }

            }
            // Footer: Comments and actions
            Text(text = "Comment amnt: ${sighting.commentCount}")
        }

    }
}
@Composable
fun VideoPlayer(url: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = androidx.media3.common.MediaItem.fromUri(url)
            setMediaItem(mediaItem)
            playWhenReady = true // You can set this to true if you want autoplay
            prepare()
        }
    }

    DisposableEffect(androidx.compose.ui.platform.LocalLifecycleOwner.current) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                setBackgroundColor(Color.Black.toArgb())
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f) // Adjust aspect ratio as needed
    )
}
