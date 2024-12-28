package com.example.project.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.project.data.model.domain.Sighting
import com.example.project.data.repository.SightingRepository
import com.example.project.service.MediaLauncherImpl
import com.example.project.service.MediaResult
import com.example.project.ui.navigation.NavigatorImpl
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NewSightingViewModel : ViewModel(), KoinComponent {

    private val navigator: NavigatorImpl by inject()
    private val sightingRepository: SightingRepository by inject()
    val mediaLauncher: MediaLauncherImpl by inject()
    val firestore: FirebaseFirestore by inject()
    private val _state = MutableStateFlow(NewSightingState())
    val state: StateFlow<NewSightingState> = _state.asStateFlow()

    private val _selectedImages = MutableStateFlow<List<String>>(emptyList())
    val selectedImages: StateFlow<List<String>> = _selectedImages.asStateFlow()

    private val _selectedVideos = MutableStateFlow<List<String>>(emptyList())
    val selectedVideos: StateFlow<List<String>> = _selectedVideos.asStateFlow()

    // Image Picker
    fun handleEvent(event: NewSightingEvent) {
        when (event) {
            is NewSightingEvent.NavigateBack -> navigator.popBackStack()
            is NewSightingEvent.PostSighting -> postSighting()
            is NewSightingEvent.UpdateContent -> updateContent(event.content)
            is NewSightingEvent.UpdateTitle -> updateTitle(event.title)
            is NewSightingEvent.HandleMediaResult -> handleMediaResult(event.result)
            is NewSightingEvent.RemoveAttachment -> removeAttachment(event.uri)
        }
    }

    private fun updateTitle(title: String) {
        _state.value = _state.value.copy(title = title)
    }

    private fun updateContent(content: String) {
        _state.value = _state.value.copy(content = content)
    }
    private fun postSighting() {
        val sightingId = firestore.collection("sightings").document().id
        val selectedMediaUris = selectedImages.value.map { Uri.parse(it) } + selectedVideos.value.map { Uri.parse(it) }

        val newSighting = Sighting(
            sightingId = sightingId,
            userId = "", // Assign actual user ID
            username = "", // Assign actual username
            location = null, // Assign location if available
            postDate = Timestamp.now(),
            sightingDate = null, // Assign sighting date if available
            description = state.value.content,
            mediaUrls = emptyList(), // Will be updated after uploading media
            title = state.value.title,
            upvotes = 0,
            downvotes = 0
        )

        // Call the repository function to upload media and save the sighting
        sightingRepository.uploadMediaAndSaveSighting(selectedMediaUris, newSighting) { success ->
            if (success) {
                // Handle success (e.g., navigate back or show a success message)
                navigator.popBackStack()
            } else {
                // Handle failure (e.g., show an error message)
            }
        }
    }
/*
    private fun postSighting(){
        val sightingId = firestore.collection("sightings").document().id
        val newSighting = Sighting(
            sightingId = sightingId,
            userId = "",
            username = "",
            location = null,
            postDate = null,
            sightingDate = null,
            description = "",
            mediaUrls =  selectedImages.value + selectedVideos.value,
            title = "",
            upvotes = 0,
            downvotes = 0
        )
    }

 */

    private fun handleMediaResult(result: MediaResult) {

        println("RESULT:::::" + result)
        when (result) {

            is MediaResult.Canceled -> {
                // Handle cancellation if needed
            }

            is MediaResult.SingleMedia -> {
                if (isImage(result.uri)) {
                    _selectedImages.update { it + result.uri.toString() }
                } else {
                    _selectedVideos.update { it + result.uri.toString() }
                }
            }

            is MediaResult.MultipleMedia -> {
                val images = result.uris.filter { isImage(it) }.map { it.toString() }
                val videos = result.uris.filter { !isImage(it) }.map { it.toString() }
                _selectedImages.update { it + images }
                _selectedVideos.update { it + videos }
            }
        }
    }

    private fun isImage(uri: Uri): Boolean {
        return uri.toString().endsWith(".jpg", ignoreCase = true) ||
                uri.toString().endsWith(".jpeg", ignoreCase = true) ||
                uri.toString().endsWith(".png", ignoreCase = true)
    }
    private fun removeAttachment(uri: String) {
        _selectedImages.update { it.filter { it != uri } }
        _selectedVideos.update { it.filter { it != uri } }
    }

}

data class NewSightingState(
    val title: String = "",
    val content: String = ""
)

sealed class NewSightingEvent {
    data class UpdateTitle(val title: String) : NewSightingEvent()
    data class UpdateContent(val content: String) : NewSightingEvent()
    object PostSighting : NewSightingEvent()
    object NavigateBack : NewSightingEvent()
    data class HandleMediaResult(val result: MediaResult) : NewSightingEvent()
    data class RemoveAttachment(val uri: String) : NewSightingEvent()
}