package com.example.project.ui.screen.sightings

import android.R
import android.content.Context
import android.media.browse.MediaBrowser
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.Uri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.project.data.model.ui.SightingCard
import com.example.project.ui.viewmodel.SightingsEvent
import com.example.project.ui.viewmodel.SightingsViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

import com.example.project.service.getRelativeTime

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {

            TopAppBar(
                title = { Text("REPORTED SIGHTINGS") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleEvent(SightingsEvent.NavigateToSettings) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }, actions = {
                    IconButton(onClick = { viewModel.handleEvent(SightingsEvent.NavigateToProfile) }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.handleEvent(SightingsEvent.NavigateToNewSighting) }) {
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
                SightingCard(sighting, context)
            }
        }
    }
}

@Composable
fun SightingCard(sighting: SightingCard, context: Context, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Location and Time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Location Icon and Text
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = sighting.location.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f) // Take up remaining space
                )

                // Time Icon and Text
                Icon(
                    imageVector = ImageVector.vectorResource(id = com.example.project.R.drawable.baseline_access_time_24),
                    contentDescription = "Time",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = getRelativeTime(sighting.postDate.toString()),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = sighting.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Username and Sighting Date
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Reporter",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Reported by ${sighting.username}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Sighting Date",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = sighting.sightingDate.toString(), // Adjust format if necessary
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Media Pager
            if (!sighting.mediaUrls.isNullOrEmpty()) {
                MediaPager(mediaList = sighting.mediaUrls, context)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Footer: Comments
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = com.example.project.R.drawable.baseline_message_24),
                    contentDescription = "Comments",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${sighting.commentCount} Comment${if (sighting.commentCount != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/*
@Composable
fun SightingCard(sighting: SightingCard, context: Context) {
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
            Box() {

                sighting.mediaUrls?.let { MediaPager(it, context) }

            }
            // Footer: Comments and actions
            Text(text = "Comment amnt: ${sighting.commentCount}")
        }

    }
}

 */
@Composable
fun MediaPager(mediaList: List<String>, context: Context) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { mediaList.size }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // Adjust as needed
        ) { index ->
            val mediaUrl = mediaList[index]
            if (mediaUrl.endsWith(".mp4") ||
                mediaUrl.contains("video", ignoreCase = true)
            ) {
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
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize(),
                    placeholder = painterResource(id = R.drawable.ic_menu_gallery),
                    error = painterResource(id = R.drawable.stat_notify_error)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.fillMaxWidth(),
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = Color.Gray,
            dotSize = 8.dp,
            activeDotSize = 10.dp,
            spacing = 4.dp
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedPagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = Color.Gray,
    dotSize: Dp = 8.dp,
    activeDotSize: Dp = 10.dp,
    spacing: Dp = 4.dp
) {
    var currentPage by remember { mutableStateOf(0) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                currentPage = page
            }
    }

    if (pagerState.pageCount > 1) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until pagerState.pageCount) {
                AnimatedContent(
                    targetState = i == currentPage,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) with
                                fadeOut(animationSpec = tween(300))
                    }
                ) { isActive ->
                    Box(
                        modifier = Modifier
                            .size(if (isActive) activeDotSize else dotSize)
                            .padding(horizontal = spacing / 2)
                            .background(
                                color = if (isActive) activeColor else inactiveColor,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun VideoPlayer(url: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(url)
            setMediaItem(mediaItem)
            playWhenReady = true // You can set this to true if you want autoplay
            prepare()
        }
    }

    DisposableEffect(LocalLifecycleOwner.current) {
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
