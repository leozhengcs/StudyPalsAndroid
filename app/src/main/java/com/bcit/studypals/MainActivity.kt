package com.bcit.studypals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bcit.studypals.ui.Background
import com.bcit.studypals.ui.theme.StudyPalsTheme

enum class Directory(val route: String) {
    HOME("home"),
    PROFILE("profile"),
    GROUPS("groups")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Put Database connection here
        enableEdgeToEdge()
        setContent {
            // Navigation
            val navController = rememberNavController()
            val studying = remember { false }

            Scaffold (
                modifier = Modifier.fillMaxSize()
            ){ padding ->
                NavHost(
                    navController = navController,
                    startDestination = Directory.HOME.route,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                ) {
                    composable(Directory.HOME.route) {
                        Home(
                            navController = navController,
                            background = Background(),
                            studying = studying
                        )
                    }

                    composable(Directory.PROFILE.route) {

                    }

                    composable(Directory.GROUPS.route) {

                    }
                }
            }

        }
    }
}
