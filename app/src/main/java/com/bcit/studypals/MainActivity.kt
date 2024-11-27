package com.bcit.studypals

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bcit.studypals.data.FirebaseRepository
import com.bcit.studypals.ui.Background
import com.bcit.studypals.ui.components.AnimatedSpriteView
import com.bcit.studypals.ui.state.FirebaseState
import com.bcit.studypals.ui.theme.StudyPalsTheme
import com.bcit.studypals.utils.ResourceProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

enum class Directory(val route: String) {
    HOME("home"),
    PROFILE("profile"),
    GROUPS("groups"),
    LANDING("landing")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Navigation
            val navController = rememberNavController()

            // For hiding nav bar in landing page
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route

            Scaffold (
                bottomBar = {
                    if (currentRoute != Directory.LANDING.route) {
                        BottomNav(navController)
                    }
                },
                modifier = Modifier.fillMaxSize()
            ){ padding ->
                NavHost(
                    navController = navController,
                    startDestination = Directory.LANDING.route,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                ) {

                    composable(Directory.LANDING.route) {
                        Landing(
                            navController
                        )
                    }

                    composable(Directory.HOME.route) {
                        Home(
                            navController = navController,
                            background = Background(),
                        )
                    }

                    composable(Directory.PROFILE.route) {
                        ProfilePage(
                            navController = navController
                        )
                    }

                    composable(Directory.GROUPS.route) {

                    }

                }
            }

        }
    }
}
