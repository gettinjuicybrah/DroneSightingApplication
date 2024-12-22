package com.example.project.data.repository
import com.example.project.data.model.domain.Location
import com.example.project.data.model.domain.Sighting
import com.example.project.data.model.domain.toLocation
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
class SightingRepository : BaseFirestoreRepository<Sighting>("sightings"), KoinComponent {

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
}