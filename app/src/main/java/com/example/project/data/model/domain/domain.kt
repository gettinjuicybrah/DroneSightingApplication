package com.example.project.data.model.domain

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

// Shared data models
data class User(
    val userId: String = "", // Firebase Auth UID
    val username: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val profileDescription: String? = null,
    val reportedSightings: List<String> = emptyList(),
    val comments: List<String> = emptyList()
)

data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

// Sightings data models
data class Sighting(
    val sightingId: String = "", // Auto-generated ID or custom
    val userId: String = "",
    val username: String = "",
    val title: String = "",
    val postDate: Timestamp? = null,
    val sightingDate: Timestamp? = null,
    val location: Location? = null,
    var mediaUrls: List<String> = emptyList(),
    val description: String? = null,
    var commentCount: Int = 0,
    var upvotes: Int = 0,
    var downvotes: Int = 0,
)

data class SightingComment(
    val commentId: String = "", // Auto-generated
    val userId: String = "",
    val username: String = "",
    val sightingId: String = "",
    val parentCommentId: String? = null,
    val content: String = "",
    val timestamp: Timestamp? = null,
    var upvotes: Int = 0,
    var downvotes: Int = 0
)


// Discussions data models
data class Discussion(
    val discussionId: String = "",// Auto-generated ID or custom
    val userId: String = "",
    val username: String = "",
    val title: String = "",
    val postDate: Timestamp? = null,
    val description: String? = null,
    var commentCount: Int = 0
)

data class DiscussionComment(
    val commentId: String = "", // Auto-generated
    val userId: String = "",
    val username: String = "",
    val discussionId: String = "",
    val parentCommentId: String? = null,
    val content: String = "",
    val timestamp: Timestamp? = null,
    var upvotes: Int = 0,
    var downvotes: Int = 0
)

// Extension functions
fun Location.toGeoPoint(): GeoPoint {
    return GeoPoint(this.latitude, this.longitude)
}

fun GeoPoint.toLocation(): Location {
    return Location(this.latitude, this.longitude)
}