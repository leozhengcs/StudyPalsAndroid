package com.bcit.studypals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bcit.studypals.data.state.FirebaseState

@Composable
fun ProfilePage(navController: NavController) {
    val firebaseVM: FirebaseState = viewModel()

    Column (
        modifier = Modifier
            .fillMaxSize()
    ) {
        Button(
            onClick = {
                firebaseVM.signOut()
                navController.navigate(Directory.LANDING.route) {
                    popUpTo(Directory.LANDING.route) {
                        inclusive = true
                    }
                }
            }
        ) {
            Text("Sign Out")
        }
    }
}