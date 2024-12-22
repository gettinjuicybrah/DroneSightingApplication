package com.example.project.data.repository

import com.example.project.data.model.domain.Sighting
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FirestoreRepository : KoinComponent {
    val firestore: FirebaseFirestore by inject()
    //....
/*
    fun postSighting(sighting: Sighting): Flow<Boolean> = callbackFlow {
        val docRef = firestore.collection("sightings").document()
        sighting.copy(sightingId = docRef.id).let {
            docRef.set(it)
                .addOnSuccessListener {
                    trySend(true)
                }
                .addOnFailureListener {
                    trySend(false)
                }
        }

        awaitClose()
    }


    fun deleteSighting(sightingId: String): Flow<Boolean> = callbackFlow {
        firestore.collection("sightings").document(sightingId).delete()
            .addOnSuccessListener {
                trySend(true)
            }
            .addOnFailureListener {
                trySend(false)
            }

        awaitClose()
    }

    fun updateSighting(sighting: Sighting): Flow<Boolean> = callbackFlow {
        firestore.collection("sightings").document(sighting.sightingId).set(sighting)
            .addOnSuccessListener {
                trySend(true)
            }
            .addOnFailureListener {
                trySend(false)
            }
        awaitClose()
    }

    fun getSightings(): Flow<List<Sighting>> = callbackFlow {
        //retrieves a collection reference to 'sightings'
        val snapshotListener = firestore.collection("sightings")
            //.orderBy("uploadTimestamp", Query.Direction.DESCENDING)
            //listens to any changes to the collection
            //snapshot: Contains a new snapshot of all of the data within the collection
            //
            //error: Contains any errors that are related to the data
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val sightings = snapshot?.documents?.mapNotNull { doc ->
                    doc.toSighting()
                } ?: emptyList()
                trySend(sightings)
            }
        awaitClose {
            snapshotListener.remove()
        }
    }

    fun getComments(sightingId: String): Flow<List<Comment>> = callbackFlow {
        val snapshotListener =
            firestore.collection("sightings").document(sightingId).collection("comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val comments = snapshot?.documents?.mapNotNull { doc ->
                        doc.toComment()
                    } ?: emptyList()
                    trySend(comments)
                }
        awaitClose {
            snapshotListener.remove()
        }
    }

    fun postComment(comment: Comment): Flow<Boolean> = callbackFlow {
        firestore.collection("sightings").document(comment.sightingId).collection("comments")
            .add(comment)
            .addOnSuccessListener {
                trySend(true)
            }
            .addOnFailureListener {
                trySend(false)
            }
        awaitClose()
    }

    fun updateComment(comment: Comment) = callbackFlow {
        firestore.collection("sightings").document(comment.sightingId).collection("comments")
            .document(comment.commentId).set(comment)
            .addOnSuccessListener {
                trySend(true)
            }
            .addOnFailureListener {
                trySend(false)
            }
        awaitClose()
    }

    fun deleteComment(sightingId: String, commentId: String): Flow<Boolean> = callbackFlow {
        firestore.collection("sightings").document(sightingId).collection("comments")
            .document(commentId).delete()
            .addOnSuccessListener {
                trySend(true)
            }
            .addOnFailureListener {
                trySend(false)
            }
        awaitClose()
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toSighting(): Sighting? {
        return try {
            val userId = getString("userId") ?: ""
            val location = getGeoPoint("location")
            val uploadTimestamp = getTimestamp("uploadTimestamp")
            val sightingTimestamp = getTimestamp("sightingTimestamp")
            val description = getString("description")
            val mediaPaths = get("mediaPaths") as? List<String>
            val comments = get("comments") as? List<HashMap<String, Any>>
            val title = getString("title") ?: ""
            val voteCount = getLong("voteCount")?.toInt() ?: 0
            val id = id

            Sighting(
                userId = userId,
                location = location,
                uploadTimestamp = uploadTimestamp,
                sightingTimestamp = sightingTimestamp,
                description = description,
                mediaPaths = mediaPaths,
                title = title,
                voteCount = voteCount,
                sightingId = id
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toComment(): Comment? {
        return try {
            val userId = getString("userId") ?: ""
            val user = getString("user") ?: ""
            val text = getString("text") ?: ""
            val timestamp = getTimestamp("timestamp")
            val sightingId = getString("sightingId") ?: ""
            val parentCommentId = getString("parentCommentId") ?: ""
            val commentId = id
            Comment(
                commentId = commentId,
                userId = userId,
                user = user,
                text = text,
                timestamp = timestamp,
                sightingId = sightingId,
                parentCommentId = parentCommentId
            )
        } catch (e: Exception) {
            null
        }
    }

 */
}


