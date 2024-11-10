package com.movie.multiplatform.compse.app


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.cash.paging.compose.collectAsLazyPagingItems
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.movie.multiplatform.compse.app.network.Movie
import com.movie.multiplatform.compse.app.network.MovieFetchType
import com.movie.multiplatform.compse.app.network.getPagedMovies
import com.movie.multiplatform.compse.app.ui.BottomNavigationBar
import com.movie.multiplatform.compse.app.ui.DrawerContent
import com.movie.multiplatform.compse.app.ui.screens.ImageSelectionSingle
import com.movie.multiplatform.compse.app.ui.screens.MovieDetailsScreen
import com.movie.multiplatform.compse.app.ui.screens.MovieListScreen
import kotlinx.coroutines.launch


sealed class AppScreen(val route: String, val title: String , val icon: ImageVector? = null) {
    data object PopularMovies : AppScreen("PopularMovies", "Popular Movies", Icons.Filled.Home)
    data object TopRatedMovies : AppScreen("TopRatedMovies", "TopRated Movies", Icons.Default.Favorite)
    data object MovieDetails : AppScreen("movieDetails/{movieId}", "Movie Details", Icons.Filled.Menu)
    data object ImageSelection : AppScreen("Image Selection", "Image Selection", Icons.Default.Settings)

    companion object {
        fun MovieDetails.createRoute(movieId: String): String {
            return "movieDetails/$movieId"
        }

        fun find(predicate: (AppScreen) -> Boolean): AppScreen? {
            return screenList.find(predicate)
        }

        val screenList: List<AppScreen>
            get() = listOf(PopularMovies,TopRatedMovies, MovieDetails, ImageSelection)
        }
    }


fun getAsyncImageLoader(context: PlatformContext) =
    ImageLoader.Builder(context).crossfade(true).logger(DebugLogger()).build()


@OptIn(ExperimentalCoilApi::class)
@Composable
fun App() {

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navController: NavHostController = rememberNavController()
    val movies = getPagedMovies(MovieFetchType.POPULAR).collectAsLazyPagingItems()
    var selectedMovie by remember { mutableStateOf<Movie?>(null) }


    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            primary = Color(0xFF3B82F6),
            primaryVariant = Color(0xFF1E3A8A),
            secondary = Color(0xFFF59E0B),
            background = Color(0xFFF3F4F6),
            surface = Color(0xFFFFFFFF),
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = Color(0xFF1F2937),
            onSurface = Color(0xFF1F2937)
        )
    ) {
        setSystemBarColor( MaterialTheme.colors.primary) // only for android

        setSingletonImageLoaderFactory { context ->
            getAsyncImageLoader(context)
        }

        ModalDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(navController, drawerState)
            }
        ) {
            val currentRoute = currentRoute(navController)
            println("Current Route: $currentRoute")

            Scaffold(
                topBar = {
                    TopAppBar(
                        contentColor = MaterialTheme.colors.onPrimary,
                        backgroundColor = MaterialTheme.colors.primary,
                        title = { Text(getTitle(currentRoute ,  selectedMovie?.title ?: "")) },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                },
                bottomBar = {
                    if (currentRoute == AppScreen.PopularMovies.route || currentRoute == AppScreen.TopRatedMovies.route || currentRoute == AppScreen.ImageSelection.route) {
                        BottomNavigationBar(navController, currentRoute ?: "")
                    }
                },
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = AppScreen.PopularMovies.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(route = AppScreen.PopularMovies.route) {
                        MovieListScreen(movies = movies, navController = navController)
                    }
                    composable(route = AppScreen.TopRatedMovies.route) {
                        MovieListScreen(movies = movies, navController = navController)
                    }
                    composable(
                        route = AppScreen.MovieDetails.route,
                        arguments = listOf(navArgument("movieId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val movieId = NavArgumentsUtil.getStringArgument(backStackEntry , "movieId")
                        selectedMovie = movies.itemSnapshotList.items.find { it.id.toString() == movieId }
                        MovieDetailsScreen(
                            movieId = movieId?.toInt() ?: 0,
                            onBack = {
                                selectedMovie = null
                                navController.popBackStack()
                            }
                        )
                    }
                    composable(route = AppScreen.ImageSelection.route) {
                        ImageSelectionSingle()
                    }

                }
            }
        }
    }
}

private fun getTitle(currentRoute : String? , selectedMovie:String ): String {
    if(currentRoute == null) return "Movies"

    if(currentRoute == AppScreen.ImageSelection.route) return "Image Selection"

    if (currentRoute == AppScreen.MovieDetails.route){
        return if(selectedMovie.isNotEmpty()) selectedMovie
        else "Movie Details"
    }



    val currentRouteName = currentRoute.split("/")?.first()
    val screenName = AppScreen.find { it.route == currentRouteName }?.title
    return screenName ?: "Movies"


}


@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}



object NavArgumentsUtil {
    fun getStringArgument(backStackEntry: NavBackStackEntry, key: String, defaultValue: String? = null): String? {
        return backStackEntry.arguments?.getString(key) ?: defaultValue
    }

    fun getIntArgument(backStackEntry: NavBackStackEntry, key: String, defaultValue: Int = 0): Int {
        return backStackEntry.arguments?.getString(key)?.toIntOrNull() ?: defaultValue
    }
}

