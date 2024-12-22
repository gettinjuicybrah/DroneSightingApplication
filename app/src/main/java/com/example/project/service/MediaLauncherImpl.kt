package com.example.project.service

import android.app.Activity
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.util.Date
import java.util.Locale


class MediaLauncherImpl : MediaLauncher {

    //added
    private var cameraLauncher: ActivityResultLauncher<Intent>? = null
    private var galleryLauncher: ActivityResultLauncher<Intent>? = null
    private var pendingCameraCallback: ((MediaResult) -> Unit)? = null
    private var pendingGalleryCallback: ((MediaResult) -> Unit)? = null
    private var currentActivity: ComponentActivity? = null
    fun initialize(activity: ComponentActivity) {
        currentActivity = activity
        cameraLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            pendingCameraCallback?.let { callback ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val mediaUri = result.data?.data
                    val context = currentActivity ?: return@let

                    if (mediaUri == null) { // Capture video does not always return data
                        val videoUri = createVideoUri(context)
                        context.contentResolver.takePersistableUriPermission(
                            videoUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                        callback(MediaResult.SingleMedia(videoUri))
                    } else {
                        context.contentResolver.takePersistableUriPermission(
                            mediaUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                        callback(MediaResult.SingleMedia(mediaUri))
                    }
                } else {
                    callback(MediaResult.Canceled)
                }
                pendingCameraCallback = null
            }
        }

        galleryLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            pendingGalleryCallback?.let { callback ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val context = currentActivity ?: return@let
                    val clipData = result.data?.clipData
                    val uris = mutableListOf<Uri>()

                    if (clipData != null) {
                        for (i in 0 until clipData.itemCount) {
                            val uri = clipData.getItemAt(i).uri
                            context.contentResolver.takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            )
                            uris.add(uri)
                        }
                    } else {
                        result.data?.data?.let { uri ->
                            context.contentResolver.takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            )
                            uris.add(uri)
                        }
                    }

                    if (uris.isNotEmpty()) {
                        callback(MediaResult.MultipleMedia(uris))
                    } else {
                        callback(MediaResult.Canceled)
                    }
                } else {
                    callback(MediaResult.Canceled)
                }
                pendingGalleryCallback = null
            }
        }
    }

    @Composable
    override fun launchCamera(onMediaCaptured: (MediaResult) -> Unit) {
        val context = currentActivity ?: return
        pendingCameraCallback = onMediaCaptured

        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, createImageUri(context))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, createVideoUri(context))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        val chooserIntent = Intent.createChooser(captureIntent, "Capture")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(videoIntent))

        cameraLauncher?.launch(chooserIntent)
    }

    @Composable
    override fun launchGallery(onMediaSelected: (MediaResult) -> Unit) {
        pendingGalleryCallback = onMediaSelected

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/* video/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        galleryLauncher?.launch(intent)
    }

    private fun createVideoUri(context: android.content.Context): Uri {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val videoFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
            "VIDEO_${timeStamp}.mp4"
        )
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", videoFile)
    }

    private fun createImageUri(context: android.content.Context): Uri {
        // Create a timestamp as part of the filename to ensure uniqueness
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        // Create a file in the pictures directory
        val imageFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IMG_${timeStamp}.jpg")
        // Return the file URI using FileProvider
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
    }

    fun cleanup() {
        currentActivity = null
        pendingCameraCallback = null
        pendingGalleryCallback = null
        cameraLauncher = null
        galleryLauncher = null
    }
}
