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

    private var cameraLauncher: ActivityResultLauncher<Intent>? = null
    private var galleryLauncher: ActivityResultLauncher<Intent>? = null
    private var pendingCameraCallback: ((MediaResult) -> Unit)? = null
    private var pendingGalleryCallback: ((MediaResult) -> Unit)? = null
    private var currentActivity: ComponentActivity? = null

    private var currentImageFile: File? = null
    private var currentImageUri: Uri? = null
    private var currentVideoFile: File? = null
    private var currentVideoUri: Uri? = null

    fun initialize(activity: ComponentActivity) {
        currentActivity = activity
        cameraLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            println("******************************************TEST_A")
            pendingCameraCallback?.let { callback ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // Check if image file exists
                    println("******************************************TEST_B")
                    val imageFileExists = currentImageFile?.exists() == true
                    val videoFileExists = currentVideoFile?.exists() == true

                    val uri = when {
                        imageFileExists -> currentImageUri
                        videoFileExists -> currentVideoUri
                        else -> null
                    }
                    if (uri != null) {
                        callback(MediaResult.SingleMedia(uri))
                    } else {
                        callback(MediaResult.Canceled)
                    }
                } else {
                    callback(MediaResult.Canceled)
                }
                println("******************************************TEST_CLEANUP")
                cleanup()
            }
        }

        galleryLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            println("******************************************TEST_GALLERY")
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
                cleanup()
            }
        }
    }

    @Composable
    override fun launchCamera(onMediaCaptured: (MediaResult) -> Unit) {
        println("******************************************LAUNCHING CAMERA.")
        val context = currentActivity ?: return
        println("******************************************TEST1")
        pendingCameraCallback = onMediaCaptured
        println("******************************************TEST2")
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, createImageUri(context))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        println("******************************************TEST3")
        val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, createVideoUri(context))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        println("******************************************TEST4")
        val chooserIntent = Intent.createChooser(captureIntent, "Capture")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(videoIntent))
        println("******************************************TEST5")
        cameraLauncher?.launch(chooserIntent)
        println("******************************************TEST6")
    }

    @Composable
    override fun launchGallery(onMediaSelected: (MediaResult) -> Unit) {
        println("******************************************LAUNCHING GALLERY.")
        val context = currentActivity ?: return
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
        val videoName = "VIDEO_${timeStamp}.mp4"
        val videosDir = File(context.filesDir, "Videos")
        if (!videosDir.exists()) {
            videosDir.mkdirs()
            println("video mkdr***********************************")
        }
        println("******************************************T10")
        val videoFile = File(videosDir, videoName)
        currentVideoFile = videoFile
        println("******************************************T11")
        val videoUri =  FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", videoFile)
        currentVideoUri = videoUri
        return videoUri
    }

    private fun createImageUri(context: android.content.Context): Uri {
        println("******************************************T7")
        // Create a timestamp as part of the filename to ensure uniqueness
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageName = "IMG_${timeStamp}.jpg"
        val imagesDir = File(context.filesDir, "Pictures")
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
            println("image mkdr***********************************")
        }
        println("******************************************T8")
        // Create a file in the pictures directory
        val imageFile = File(imagesDir, imageName)
        currentImageFile = imageFile
        println("******************************************T9")
        // Return the file URI using FileProvider
        val imageUri =
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
        currentImageUri = imageUri

        return imageUri
    }

    fun cleanup() {
        pendingCameraCallback = null
        pendingGalleryCallback = null
        //cameraLauncher = null
        //galleryLauncher = null
        currentImageFile = null
        currentImageUri = null
        currentVideoFile = null
        currentVideoUri = null

    }
}
