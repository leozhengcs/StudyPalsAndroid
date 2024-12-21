package com.bcit.studypals

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bcit.studypals.data.state.FirebaseState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun Landing(navController: NavController) {
    val firebaseVM: FirebaseState = viewModel()

    val isSignedIn = firebaseVM.isSignedIn.collectAsState().value
    val userName = firebaseVM.userName.collectAsState().value

    // Remember launcher for Google Sign-In result
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let {idToken ->
                firebaseVM.firebaseAuthWithGoogle(idToken)
            }
        } catch (e: ApiException) {
            Log.w("Landing", "Google sign-in failed", e)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.landing),
                contentDescription = null,
                contentScale = ContentScale.FillHeight
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(color = Color.Black.copy(alpha = 0.5f))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isSignedIn) {
            Text(text = "Welcome, $userName!")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                navController.navigate(Directory.HOME.route)
            }) {
                Text("Go to App")
            }
            Button(onClick = {
                firebaseVM.signOut()
            }) {
                Text("Sign Out")
            }
        } else {
            Button(onClick = {
                googleSignInLauncher.launch(firebaseVM.getGoogleSignInClient().signInIntent)
            }) {
                Text("Sign In with Google")
            }
        }

    }
}