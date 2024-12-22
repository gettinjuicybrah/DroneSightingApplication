package com.example.project.data.model.ui

import com.example.project.data.model.domain.Location
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint


data class SightingCard(
    val sightingId: String = "",
    val userId: String = "",
    val username: String = "",
    val location: Location? = null,
    val postDate: Timestamp? = null,
    val sightingDate: Timestamp? = null,
    val description: String? = null,
    val mediaUrls: List<String>? = null,
    val commentCount: Int = 0,
    val title: String = "",
    val upvotes: Int = 0,
    val downvotes: Int = 0
)

data class CommentCard(
    val commentId: String = "",
    val userId: String = "",
    val user: String = "",
    val text: String = "",
    val timestamp: Timestamp? = null,
    val sightingId: String = "",
    val parentCommentId: String = ""
)