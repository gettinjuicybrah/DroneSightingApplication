package com.example.project.data.repository

import com.example.project.data.model.domain.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
/**
 * Repository for managing users in Firestore.
 * Extends BaseFirestoreRepository to inherit common Firestore operations.
 */
class UserRepository : BaseFirestoreRepository<User>("users"), KoinComponent {
    /**
     * Converts a Firestore document into a User domain model.
     *
     * @param document The Firestore document snapshot.
     * @return A User instance or null if conversion fails.
     */
    override fun fromDocument(document: DocumentSnapshot): User? {
        return try {
            val userId = document.id
            val username = document.getString("username") ?: ""
            val email = document.getString("email") ?: ""
            val profileImageUrl = document.getString("profileImageUrl")
            val profileDescription = document.getString("profileDescription")
            val reportedSightings = document.get("reportedSightings") as? List<String> ?: emptyList()
            val comments = document.get("comments") as? List<String> ?: emptyList()

            User(
                userId = userId,
                username = username,
                email = email,
                profileImageUrl = profileImageUrl,
                profileDescription = profileDescription,
                reportedSightings = reportedSightings,
                comments = comments
            )

        } catch (e: Exception) {
            null
        }
    }
}