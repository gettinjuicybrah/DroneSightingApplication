package com.example.project.data.repository
import com.example.project.data.model.domain.SightingComment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
/**
 * Repository for managing sighting comments in Firestore.
 * Extends BaseFirestoreRepository to inherit common Firestore operations.
 */
class SightingCommentRepository : BaseFirestoreRepository<SightingComment>("sightings"), KoinComponent {
    /**
     * Converts a Firestore document into a SightingComment domain model.
     *
     * @param document The Firestore document snapshot.
     * @return A SightingComment instance or null if conversion fails.
     */
    override fun fromDocument(document: DocumentSnapshot): SightingComment? {
        return try {
            val commentId = document.id
            val userId = document.getString("userId") ?: ""
            val username = document.getString("username") ?: ""
            val sightingId = document.getString("sightingId") ?: ""
            val parentCommentId = document.getString("parentCommentId")
            val content = document.getString("content") ?: ""
            val timestamp = document.getTimestamp("timestamp")
            val upvotes = document.getLong("upvotes")?.toInt() ?: 0
            val downvotes = document.getLong("downvotes")?.toInt() ?: 0

            SightingComment(
                commentId = commentId,
                userId = userId,
                username = username,
                sightingId = sightingId,
                parentCommentId = parentCommentId,
                content = content,
                timestamp = timestamp,
                upvotes = upvotes,
                downvotes = downvotes
            )
        } catch (e: Exception) {
            null
        }
    }
    /**
     * Retrieves all comments for a specific sighting from its "sightingComments" subcollection.
     *
     * @param sightingId The ID of the sighting.
     * @param orderBy Optional field to order the results by.
     * @param direction Optional direction for ordering.
     * @return A Flow emitting a list of SightingComment objects.
     */
    fun getSightingComments(sightingId: String, orderBy: String? = null, direction: Query.Direction? = null):  Flow<List<SightingComment>> {
        return getSubCollection(sightingId, "sightingComments", orderBy, direction)
    }
}