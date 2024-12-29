package com.example.project.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
/**
 * Abstract base class for Firestore repositories.
 * Provides common CRUD operations for interacting with a specified Firestore collection.
 * Subclasses must implement the `fromDocument` method to convert Firestore documents into their specific domain models.
 *
 * @param T The type of the domain model, must be a subclass of Any.
 * @param collectionName The name of the Firestore collection this repository operates on.
 */
abstract class BaseFirestoreRepository<T : Any>(private val collectionName: String) :
    KoinComponent {
    // Inject FirebaseFirestore instance using Koin dependency injection.
    val firestore: FirebaseFirestore by inject()

    /**
     * Abstract method to convert a Firestore document into a domain model.
     * Subclasses must implement this to map document fields to their specific model.
     *
     * @param document The Firestore document snapshot to convert.
     * @return The domain model instance, or null if conversion fails.
     */
    abstract fun fromDocument(document: DocumentSnapshot): T?
    /**
     * Posts a new item to the Firestore collection.
     * If a document ID is provided, it uses that ID; otherwise, a new ID is generated.
     *
     * @param item The item to post.
     * @param documentId Optional ID for the document; if null, a new ID is generated.
     * @return A Flow emitting true on success, false on failure.
     */
    fun post(item: T, documentId: String? = null): Flow<Boolean> = callbackFlow {
        val docRef = if (documentId != null) {
            firestore.collection(collectionName).document(documentId)
        } else {
            firestore.collection(collectionName).document()
        }

        docRef.set(item).addOnSuccessListener {
            trySend(true)
        }.addOnFailureListener {
            trySend(false)
        }

        awaitClose()
    }

    /**
     * Deletes a document from the Firestore collection by its ID.
     *
     * @param documentId The ID of the document to delete.
     * @return A Flow emitting true on success, false on failure.
     */
    fun delete(documentId: String): Flow<Boolean> = callbackFlow {
        firestore.collection(collectionName).document(documentId).delete()
            .addOnSuccessListener { trySend(true) }
            .addOnFailureListener { trySend(false) }
        awaitClose()
    }
    /**
     * Updates an existing document in the Firestore collection.
     *
     * @param documentId The ID of the document to update.
     * @param item The updated item data.
     * @return A Flow emitting true on success, false on failure.
     */
    fun update(documentId: String, item: T): Flow<Boolean> = callbackFlow {
        firestore.collection(collectionName).document(documentId).set(item)
            .addOnSuccessListener { trySend(true) }
            .addOnFailureListener { trySend(false) }
        awaitClose()
    }

    /**
     * Retrieves all documents from the Firestore collection, with optional ordering.
     *
     * @param orderBy Optional field to order the results by.
     * @param direction Optional direction for ordering (e.g., ASCENDING or DESCENDING).
     * @return A Flow emitting a list of domain models.
     */
    fun getAll(orderBy: String? = null, direction: Query.Direction? = null): Flow<List<T>> = callbackFlow {
        var query = firestore.collection(collectionName)

        if (orderBy != null && direction != null) {
            query = query.orderBy(orderBy, direction) as CollectionReference
        }

        val snapshotListener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val items = snapshot?.documents?.mapNotNull { doc ->
                fromDocument(doc)
            } ?: emptyList()
            trySend(items)
        }
        awaitClose {
            snapshotListener.remove()
        }
    }
    /**
     * Retrieves a single document from the Firestore collection by its ID.
     *
     * @param documentId The ID of the document to retrieve.
     * @return A Flow emitting the domain model or null if not found.
     */
    fun get(documentId: String): Flow<T?> = callbackFlow {
        val documentRef = firestore.collection(collectionName).document(documentId)

        val snapshotListener = documentRef.addSnapshotListener{ snapshot, error ->
            if(error != null){
                close(error)
                return@addSnapshotListener
            }
            val item = snapshot?.let { fromDocument(it) }
            trySend(item)
        }
        awaitClose {
            snapshotListener.remove()
        }
    }
    /**
     * Retrieves all documents from a subcollection of a specific document, with optional ordering.
     *
     * @param documentId The ID of the parent document.
     * @param subCollectionName The name of the subcollection.
     * @param orderBy Optional field to order the results by.
     * @param direction Optional direction for ordering.
     * @return A Flow emitting a list of domain models.
     */
    fun getSubCollection(documentId: String, subCollectionName: String, orderBy: String? = null, direction: Query.Direction? = null): Flow<List<T>> = callbackFlow {
        var query = firestore.collection(collectionName).document(documentId).collection(subCollectionName)

        if(orderBy != null && direction != null) {
            query = query.orderBy(orderBy, direction) as CollectionReference
        }

        val snapshotListener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull { doc ->
                fromDocument(doc)
            } ?: emptyList()
            trySend(items)
        }
        awaitClose {
            snapshotListener.remove()
        }
    }

}