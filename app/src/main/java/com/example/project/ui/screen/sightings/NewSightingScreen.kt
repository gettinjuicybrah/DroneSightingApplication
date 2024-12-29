package com.example.project.ui.screen.sightings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import coil3.Image
import com.example.project.R
import com.example.project.ui.viewmodel.NewSightingEvent
import com.example.project.ui.viewmodel.NewSightingViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

import android.net.Uri
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun NewSightingScreen() {
    val viewModel: NewSightingViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    // Collect lists of selected images and videos from the ViewModel
    val images = viewModel.selectedImages.collectAsState().value
    val videos = viewModel.selectedVideos.collectAsState().value

    // Access the media launcher from the ViewModel for capturing or selecting media
    val mediaLauncher = viewModel.mediaLauncher
    // State to control the visibility of the media attachment menu
    var showMenu by remember { mutableStateOf(false) }
    // State to determine which media type to launch (camera or gallery)
    var launchMedia by remember { mutableStateOf<MediaLaunchType?>(null) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Drone Sighting") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleEvent(NewSightingEvent.NavigateBack) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Pop backstack"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.handleEvent(NewSightingEvent.PostSighting) }) {
                        Text("Post")
                    }
                    IconButton(onClick = {
                        showMenu = true
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_attach_file_24),
                            contentDescription = "Attach file."
                        )
                    }
                    // Dropdown menu for selecting media source (camera or gallery)
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Camera") },
                            onClick = {
                                showMenu = false
                                launchMedia = MediaLaunchType.Camera
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Gallery") },
                            onClick = {
                                showMenu = false
                                launchMedia = MediaLaunchType.Gallery
                            }
                        )
                    }
                }
            )
        }) { paddingValues ->
        // Column arranges UI elements vertically with padding
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))
            // Displays thumbnails of attached images and videos in a horizontal scrollable row
            if (images.isNotEmpty() || videos.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        items(images + videos) { uri ->
                            AttachmentThumbnail(
                                uri = uri,
                                onRemove = {
                                    viewModel.handleEvent(NewSightingEvent.RemoveAttachment(uri))
                                }
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = state.title,
                onValueChange = { viewModel.handleEvent(NewSightingEvent.UpdateTitle(it)) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = state.content,
                onValueChange = { viewModel.handleEvent(NewSightingEvent.UpdateContent(it)) },
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = Int.MAX_VALUE
            )
        }
    }
    // Handles media launching based on user selection from the menu
    when (launchMedia) {
        MediaLaunchType.Camera -> {
            mediaLauncher.launchCamera { result ->
                viewModel.handleEvent(NewSightingEvent.HandleMediaResult(result))
            }
            launchMedia = null
        }

        MediaLaunchType.Gallery -> {
            mediaLauncher.launchGallery { result ->
                viewModel.handleEvent(NewSightingEvent.HandleMediaResult(result))
            }
            launchMedia = null
        }

        null -> {} // No action if no media launch is requested

    }
}
// Composable function to display a thumbnail of an attached media file with a remove option
@Composable
private fun AttachmentThumbnail(
    uri: String,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .height(80.dp)
            .width(80.dp)
    ) {
        // Load and display the media
        AsyncImage(
            model = Uri.parse(uri),
            contentDescription = "Media attachment",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Icon button to remove the attachment
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                .size(24.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove attachment",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
// Enum to define types of media launches
enum class MediaLaunchType {
    Camera, Gallery
}