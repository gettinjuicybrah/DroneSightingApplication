package com.example.project.service

import android.net.Uri
import androidx.compose.runtime.Composable

//Will be expect/actual pattern when moving to Kotlin multiplatform
interface MediaLauncher {
    /**
     * Launches the camera to capture either an image or a video.
     * @param onMediaCaptured Callback with the captured media URI or null if cancelled/failed.
     */
    @Composable
    fun launchCamera(onMediaCaptured: (MediaResult) -> Unit)

    /**
     * Launches the gallery to select multiple images and/or videos.
     * @param onMediaSelected Callback with a list of selected media URIs or null if cancelled/failed.
     */
    @Composable
    fun launchGallery(onMediaSelected: (MediaResult) -> Unit)
}
sealed class MediaResult {
    data class SingleMedia(val uri: Uri) : MediaResult()
    data class MultipleMedia(val uris: List<Uri>) : MediaResult()
    object Canceled : MediaResult()
}

interface MediaLauncherFactory{
    fun create() : MediaLauncher
}