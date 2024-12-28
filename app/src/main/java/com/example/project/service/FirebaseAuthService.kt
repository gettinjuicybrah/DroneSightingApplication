package com.example.project.service

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import org.koin.core.component.KoinComponent

class FirebaseAuthService: KoinComponent {

    private val firebaseAuth = FirebaseAuth.getInstance()

    /*
    val properties with custom getters in Kotlin are read-only references, not immutable
    values.
     */
    //val indicates that isLoggedIn is read-only. Cannot directly assign a value to
    //isLoggedIn after its declaration.

    val isLoggedIn: Boolean
        //This defines a custom getter for isLoggedIn. Every time isLoggedIn is accessed,
        //the code inside the getter is executed.
        get() = firebaseAuth.currentUser != null

    val currentUser
        get() = firebaseAuth.currentUser

    fun registerUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration success
                    Log.d(TAG, "createUserWithEmail:success")
                    onResult(true, null)
                } else {
                    // Registration failure
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login success
                    Log.d(TAG, "signInWithEmail:success")
                    onResult(true, null)
                } else {
                    // Login failure
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        firebaseAuth.addAuthStateListener(listener)
    }

    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        firebaseAuth.removeAuthStateListener(listener)
    }
}