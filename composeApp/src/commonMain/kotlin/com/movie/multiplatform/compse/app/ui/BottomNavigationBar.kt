package com.movie.multiplatform.compse.app.ui

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.movie.multiplatform.compse.app.AppScreen
import com.movie.multiplatform.compse.app.currentRoute


@Composable
fun BottomNavigationBar(navController: NavController , currentRoute: String) {
    val items = listOf(AppScreen.PopularMovies, AppScreen.TopRatedMovies , AppScreen.ImageSelection)
    BottomNavigation {

        items.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = screen.icon ?: Icons.Default.Create,

                        contentDescription = screen.route
                    )
                },
                label = { Text(screen.route) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationRoute!!) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}