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

class UserRepository : BaseFirestoreRepository<User>("users"), KoinComponent {
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