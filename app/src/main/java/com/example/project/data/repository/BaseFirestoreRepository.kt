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

abstract class BaseFirestoreRepository<T : Any>(private val collectionName: String) :
    KoinComponent {
    val firestore: FirebaseFirestore by inject()

    abstract fun fromDocument(document: DocumentSnapshot): T?

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


    fun delete(documentId: String): Flow<Boolean> = callbackFlow {
        firestore.collection(collectionName).document(documentId).delete()
            .addOnSuccessListener { trySend(true) }
            .addOnFailureListener { trySend(false) }
        awaitClose()
    }

    fun update(documentId: String, item: T): Flow<Boolean> = callbackFlow {
        firestore.collection(collectionName).document(documentId).set(item)
            .addOnSuccessListener { trySend(true) }
            .addOnFailureListener { trySend(false) }
        awaitClose()
    }


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