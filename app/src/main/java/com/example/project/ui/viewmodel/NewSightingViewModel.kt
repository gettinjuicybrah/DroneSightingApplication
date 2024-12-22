package com.example.project.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.service.MediaLauncher
import com.example.project.service.MediaLauncherImpl
import com.example.project.service.MediaResult
import com.example.project.ui.navigation.NavigatorImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NewSightingViewModel : ViewModel(), KoinComponent {

    private val navigator: NavigatorImpl by inject()

    val mediaLauncher: MediaLauncherImpl by inject()

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
            is NewSightingEvent.PostSighting -> TODO()
            is NewSightingEvent.UpdateContent -> TODO()
            is NewSightingEvent.UpdateTitle -> TODO()
            is NewSightingEvent.HandleMediaResult -> handleMediaResult(event.result)
            is NewSightingEvent.RemoveAttachment -> removeAttachment(event.uri)
        }
    }

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