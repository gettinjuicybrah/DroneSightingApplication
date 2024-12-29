package com.example.project.data.repository
import android.net.Uri
import com.example.project.data.model.domain.Location
import com.example.project.data.model.domain.Sighting
import com.example.project.data.model.domain.toLocation
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import org.koin.core.component.KoinComponent
/**
 * Repository for managing sightings in Firestore.
 * Extends BaseFirestoreRepository to inherit common Firestore operations.
 * Includes additional functionality for uploading media files to Firebase Storage.
 */
class SightingRepository : BaseFirestoreRepository<Sighting>("sightings"), KoinComponent {
    // Initialize FirebaseStorage instance for handling media uploads.
    val firebaseStorage = FirebaseStorage.getInstance()
    /**
     * Converts a Firestore document into a Sighting domain model.
     *
     * @param document The Firestore document snapshot.
     * @return A Sighting instance or null if conversion fails.
     */
    override fun fromDocument(document: DocumentSnapshot): Sighting? {
        return try {
            val sightingId = document.id
            val userId = document.getString("userId") ?: ""
            val username = document.getString("username") ?: ""
            val title = document.getString("title") ?: ""
            val postDate = document.getTimestamp("postDate")
            val sightingDate = document.getTimestamp("sightingDate")
            val location = document.getGeoPoint("location")?.toLocation() ?: Location()
            val mediaUrls = document.get("mediaUrls") as? List<String> ?: emptyList()
            val description = document.getString("description")
            val commentCount = document.getLong("commentCount")?.toInt() ?: 0
            val upvotes = document.getLong("upvotes")?.toInt() ?: 0
            val downvotes = document.getLong("downvotes")?.toInt() ?: 0


            Sighting(
                sightingId = sightingId,
                userId = userId,
                username = username,
                title = title,
                postDate = postDate,
                sightingDate = sightingDate,
                location = location,
                mediaUrls = mediaUrls,
                description = description,
                commentCount = commentCount,
                upvotes = upvotes,
                downvotes = downvotes
            )
        } catch (e: Exception) {
            null
        }
    }
    /**
     * Uploads media files to Firebase Storage and saves the sighting data to Firestore.
     * If no files are provided, it saves the sighting directly.
     *
     * @param fileUris List of URIs for media files to upload.
     * @param sighting The sighting data to save.
     * @param onComplete Callback invoked with true on success, false on failure.
     */
    fun uploadMediaAndSaveSighting(
        fileUris: List<Uri>,
        sighting: Sighting,
        onComplete: (Boolean) -> Unit
    ) {
        val downloadUrls = mutableListOf<String>()
        val totalFiles = fileUris.size
        var filesProcessed = 0
        var isErrorOccurred = false

        if (totalFiles == 0) {
            // No files to upload, just save the sighting data
            firestore.collection("sightings").document(sighting.sightingId)
                .set(sighting)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
            return
        }

        for (fileUri in fileUris) {
            val fileName = "${System.currentTimeMillis()}_${fileUri.lastPathSegment}"
            val storageRef = firebaseStorage.reference.child("sightings-media/$fileName")
            val uploadTask = storageRef.putFile(fileUri)

            uploadTask
                .addOnSuccessListener {
                    storageRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            downloadUrls.add(downloadUrl)
                            filesProcessed++

                            if (filesProcessed == totalFiles && !isErrorOccurred) {
                                // All files uploaded successfully
                                val updatedSighting = sighting.copy(mediaUrls = downloadUrls)
                                firestore.collection("sightings").document(sighting.sightingId)
                                    .set(updatedSighting)
                                    .addOnSuccessListener { onComplete(true) }
                                    .addOnFailureListener { onComplete(false) }
                            }
                        }
                        .addOnFailureListener {
                            if (!isErrorOccurred) {
                                isErrorOccurred = true
                                onComplete(false)
                            }
                        }
                }
                .addOnFailureListener {
                    if (!isErrorOccurred) {
                        isErrorOccurred = true
                        onComplete(false)
                    }
                }
        }
    }

}