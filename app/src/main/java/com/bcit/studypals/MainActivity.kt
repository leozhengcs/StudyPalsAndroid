package com.bcit.studypals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bcit.studypals.ui.Background

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
