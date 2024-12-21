package com.bcit.studypals.data.state

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.bcit.studypals.data.repositories.FirebaseRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FirebaseState(application: Application) : AndroidViewModel(application) {
    private val firebaseRepository: FirebaseRepository = FirebaseRepository(application)
    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val firebaseAuth = firebaseRepository.getFirebaseAuth()
    private val firestore = firebaseRepository.getFirestore()

    init {
        // Check if the user is already signed in
        firebaseAuth.currentUser?.let { user ->
            _isSignedIn.value = true
            _userName.value = user.displayName ?: "User"
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false

                    val onSuccess: () -> Unit = {Log.d("Firebase", "New user created")}
                    val onFail: (Exception) -> Unit = {
                        Log.e("Exception", it.message ?: "An error happened lol")
                    }

                    if (isNewUser) {
                        firebaseRepository.createNewUser(user!!, onSuccess, onFail)
                    }

                    user?.let {
                        _isSignedIn.value = true
                        _userName.value = it.displayName ?: "User"
                    }
                } else {
                    task.exception?.let {
                        // Handle error
                    }
                }
            }
    }

    fun signOut() {
        firebaseAuth.signOut()
        getGoogleSignInClient().signOut()
        _isSignedIn.value = false
        _userName.value = ""
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        return firebaseRepository.getGoogleSignInClient()
    }

    // Returns user that is logged in or null
    fun getCurrentUser(): FirebaseUser? {
        return firebaseRepository.getCurrentUser()
    }

}