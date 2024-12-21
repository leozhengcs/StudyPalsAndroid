package com.bcit.studypals.data.repositories

import android.app.Application
import android.util.Log
import com.bcit.studypals.R
import com.bcit.studypals.utils.ResourceProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository(val application: Application) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun signInWithGoogleCredential(
        credential: AuthCredential,
        onComplete: (FirebaseUser?, Boolean, Exception?) -> Unit
    ) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false
                    onComplete(user, isNewUser, null)
                } else {
                    onComplete(null, false, task.exception)
                }
            }
    }

    fun createNewUser(user: FirebaseUser, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // TODO: Add a username screen that allows the user to friend via username if creating a new user.
        val userData = mapOf(
            "uid" to user.uid,
            "name" to user.displayName,
            "email" to user.email,
            "current_pet" to "fox",
            "points" to 0,
            "study_hours" to 0,
        )

        firestore.collection("users").document(user.uid)
            .set(userData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getFirebaseAuth(): FirebaseAuth {
        return firebaseAuth
    }

    fun getFirestore(): FirebaseFirestore {
        return firestore
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        val resourceProvider = ResourceProvider(application)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(resourceProvider.getString(R.string.web_api_key))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(application, gso)
    }
}