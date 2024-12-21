package com.bcit.studypals

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class NavItem(val icon_id: Int, val navRoute: String)

@Composable
fun BottomNav(navController: NavController) {

    val navItems = listOf(
        NavItem(R.drawable.icon_groups, Directory.GROUPS.route),
        NavItem(R.drawable.icon_home, Directory.HOME.route),
        NavItem(R.drawable.icon_profile, Directory.PROFILE.route)
    )

    NavigationBar (
        modifier = Modifier
            .height(75.dp)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        navItems.forEach {
            NavigationBarItem(
                selected = currentRoute == it.navRoute,
                onClick = {
                    navController.navigate(it.navRoute)
                },
                icon = {
                    Image(
                        painterResource(it.icon_id),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
//                    indicatorColor = Color.Transparent // Removes the darkened circle
                )
            )
        }
    }
}