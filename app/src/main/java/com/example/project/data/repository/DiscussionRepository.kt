package com.example.project.data.repository
import com.example.project.data.model.domain.Discussion
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
/**
 * Repository for managing discussions in Firestore.
 * Extends BaseFirestoreRepository to inherit common Firestore operations.
 */
class DiscussionRepository : BaseFirestoreRepository<Discussion>("discussions"), KoinComponent {
    /**
     * Converts a Firestore document into a Discussion domain model.
     *
     * @param document The Firestore document snapshot.
     * @return A Discussion instance or null if conversion fails.
     */
    override fun fromDocument(document: DocumentSnapshot): Discussion? {
        return try {
            val discussionId = document.id
            val userId = document.getString("userId") ?: ""
            val username = document.getString("username") ?: ""
            val title = document.getString("title") ?: ""
            val postDate = document.getTimestamp("postDate")
            val description = document.getString("description")
            val commentCount = document.getLong("commentCount")?.toInt() ?: 0

            Discussion(
                discussionId = discussionId,
                userId = userId,
                username = username,
                title = title,
                postDate = postDate,
                description = description,
                commentCount = commentCount
            )
        } catch (e: Exception) {
            null
        }
    }
}
