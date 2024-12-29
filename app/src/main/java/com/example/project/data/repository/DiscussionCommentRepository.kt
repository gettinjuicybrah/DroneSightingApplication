package com.example.project.data.repository
import com.example.project.data.model.domain.Discussion
import com.example.project.data.model.domain.DiscussionComment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Repository for managing discussion comments in Firestore.
 * Extends BaseFirestoreRepository to inherit common Firestore operations.
 */
class DiscussionCommentRepository : BaseFirestoreRepository<DiscussionComment>("discussions") {
    /**
     * Converts a Firestore document into a DiscussionComment domain model.
     *
     * @param document The Firestore document snapshot.
     * @return A DiscussionComment instance or null if conversion fails.
     */
    override fun fromDocument(document: DocumentSnapshot): DiscussionComment? {
        return try {
            val commentId = document.id
            val userId = document.getString("userId") ?: ""
            val username = document.getString("username") ?: ""
            val discussionId = document.getString("discussionId") ?: ""
            val parentCommentId = document.getString("parentCommentId")
            val content = document.getString("content") ?: ""
            val timestamp = document.getTimestamp("timestamp")
            val upvotes = document.getLong("upvotes")?.toInt() ?: 0
            val downvotes = document.getLong("downvotes")?.toInt() ?: 0

            DiscussionComment(
                commentId = commentId,
                userId = userId,
                username = username,
                discussionId = discussionId,
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
     * Retrieves all comments for a specific discussion from its "discussionComments" subcollection.
     *
     * @param discussionId The ID of the discussion.
     * @param orderBy Optional field to order the results by.
     * @param direction Optional direction for ordering.
     * @return A Flow emitting a list of DiscussionComment objects.
     */
    fun getDiscussionComments(discussionId: String, orderBy: String? = null, direction: Query.Direction? = null): Flow<List<DiscussionComment>> {
        return getSubCollection(discussionId, "discussionComments", orderBy, direction)
    }
}