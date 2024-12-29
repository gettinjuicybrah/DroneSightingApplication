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
/**
 * ViewModel for creating and managing a new sighting post.
 * Handles title, content, media attachments, and posting to the repository.
 */
class NewSightingViewModel : ViewModel(), KoinComponent {

    private val navigator: NavigatorImpl by inject()
    private val sightingRepository: SightingRepository by inject()
    val mediaLauncher: MediaLauncherImpl by inject()
    val firestore: FirebaseFirestore by inject()
    // State flow for the sighting's title and content
    private val _state = MutableStateFlow(NewSightingState())
    val state: StateFlow<NewSightingState> = _state.asStateFlow()

    // State flow for tracking selected image URIs
    private val _selectedImages = MutableStateFlow<List<String>>(emptyList())
    val selectedImages: StateFlow<List<String>> = _selectedImages.asStateFlow()

    // State flow for tracking selected video URIs
    private val _selectedVideos = MutableStateFlow<List<String>>(emptyList())
    val selectedVideos: StateFlow<List<String>> = _selectedVideos.asStateFlow()

    /**
     * Handles events related to creating a new sighting.
     * Manages navigation, updates, media selection, and posting.
     */
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
    /**
     * Updates the title of the new sighting.
     * @param title The new title value from the UI.
     */
    private fun updateTitle(title: String) {
        _state.value = _state.value.copy(title = title)
    }
    /**
     * Updates the content (description) of the new sighting.
     * @param content The new content value from the UI.
     */
    private fun updateContent(content: String) {
        _state.value = _state.value.copy(content = content)
    }
    /**
     * Posts the new sighting to the repository after uploading associated media.
     * Creates a Sighting object and uses the repository to save it.
     */
    private fun postSighting() {
        // Generate a unique ID for the sighting
        val sightingId = firestore.collection("sightings").document().id
        // Combine selected images and videos into a list of URIs
        val selectedMediaUris = selectedImages.value.map { Uri.parse(it) } + selectedVideos.value.map { Uri.parse(it) }

        // Construct a new Sighting object with current state
        val newSighting = Sighting(
            sightingId = sightingId,
            userId = "",          // TODO: Replace with actual user ID
            username = "",        // TODO: Replace with actual username
            location = null,      // TODO: Add location data if available
            postDate = Timestamp.now(),  // Current timestamp for posting
            sightingDate = null,  // TODO: Add sighting date if applicable
            description = state.value.content,
            mediaUrls = emptyList(),  // Updated by repository after upload
            title = state.value.title,
            upvotes = 0,
            downvotes = 0
        )

        // Upload media and save sighting via the repository
        sightingRepository.uploadMediaAndSaveSighting(selectedMediaUris, newSighting) { success ->
            if (success) {
                // Navigate back on successful post
                navigator.popBackStack()
            } else {
                // TODO: Handle failure (e.g., display error message)
            }
        }
    }
    /**
     * Processes the result of media selection from the media launcher.
     * Adds selected media to either images or videos based on type.
     * @param result The result from the media picker.
     */
    private fun handleMediaResult(result: MediaResult) {

        println("RESULT:::::" + result)
        when (result) {

            is MediaResult.Canceled -> {
                // No action needed for cancellation
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
    /**
     * Determines if a URI represents an image file based on its extension.
     * @param uri The URI to check.
     * @return True if the URI is an image (jpg, jpeg, png), false otherwise.
     */
    private fun isImage(uri: Uri): Boolean {
        return uri.toString().endsWith(".jpg", ignoreCase = true) ||
                uri.toString().endsWith(".jpeg", ignoreCase = true) ||
                uri.toString().endsWith(".png", ignoreCase = true)
    }
    /**
     * Removes a media attachment from the selected images or videos list.
     * @param uri The URI of the media to remove.
     */
    private fun removeAttachment(uri: String) {
        _selectedImages.update { it.filter { it != uri } }
        _selectedVideos.update { it.filter { it != uri } }
    }

}
/**
 * Data class representing the state of the new sighting screen.
 * Holds the title and content of the sighting.
 */
data class NewSightingState(
    val title: String = "",
    val content: String = ""
)
/**
 * Sealed class defining possible events for the new sighting screen.
 * Used to communicate UI actions to the ViewModel.
 */
sealed class NewSightingEvent {
    data class UpdateTitle(val title: String) : NewSightingEvent()
    data class UpdateContent(val content: String) : NewSightingEvent()
    object PostSighting : NewSightingEvent()
    object NavigateBack : NewSightingEvent()
    data class HandleMediaResult(val result: MediaResult) : NewSightingEvent()
    data class RemoveAttachment(val uri: String) : NewSightingEvent()
}