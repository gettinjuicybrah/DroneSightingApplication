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

class SightingRepository : BaseFirestoreRepository<Sighting>("sightings"), KoinComponent {
    val firebaseStorage = FirebaseStorage.getInstance()
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
/*
            val downloadUrls = mediaUrls.map { mediaUrl ->
                val storageRef = firebaseStorage.reference.child(mediaUrl) // Replace with path construction logic
                storageRef.getDownloadUrl() // This will block the coroutine, consider using async/await
            }

 */

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
    /*
    fun uploadImageAndSaveUrl(
        fileUri: Uri,
        sightingId: String,
        sightingData: Map<String, Any?>,
        onComplete: (Boolean) -> Unit
    ) {
        val storageRef = firebaseStorage.reference.child("sightings-media/${fileUri.lastPathSegment}")
        val uploadTask = storageRef.putFile(fileUri)
        uploadTask
            .addOnSuccessListener {
                storageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        // Update Firestore document with the download URL and other sighting data
                        val updatedSightingData = sightingData.toMutableMap()
                        updatedSightingData["mediaUrls"] = FieldValue.arrayUnion(downloadUrl)
                        firestore.collection("sightings").document(sightingId)
                            .set(updatedSightingData, SetOptions.merge())
                            .addOnSuccessListener { onComplete(true) }
                            .addOnFailureListener { onComplete(false) }
                    }
                    .addOnFailureListener { onComplete(false) }
            }
            .addOnFailureListener { onComplete(false) }
    }
     */
}