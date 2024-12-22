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
class DiscussionRepository : BaseFirestoreRepository<Discussion>("discussions"), KoinComponent {
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
