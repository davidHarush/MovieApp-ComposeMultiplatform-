package com.movie.multiplatform.compse.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.movie.multiplatform.compse.app.AppScreen
import kotlinx.coroutines.launch


@Composable
fun DrawerContent(navController: NavHostController, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(MaterialTheme.colors.surface)
            .padding(16.dp)
    ) {
        Text(
            text = "Menu",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Text(
            text = "Home",
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    scope.launch {
                        navController.navigate(AppScreen.PopularMovies.route)
                        drawerState.close()
                    }
                }
                .padding(vertical = 12.dp)
        )
        Text(
            text = "Image Selection test",
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    scope.launch {
                        navController.navigate(AppScreen.ImageSelection.route)
                        drawerState.close()
                    }
                }
                .padding(vertical = 12.dp)
        )
    }
}