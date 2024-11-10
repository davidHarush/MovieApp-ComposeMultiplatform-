//package com.movie.multiplatform.compse.app
//
//
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//import com.movie.multiplatform.compse.app.network.Movie
//import com.movie.multiplatform.compse.app.network.MovieRepository
//import dev.icerock.moko.mvvm.viewmodel.ViewModel
//import io.github.aakira.napier.Napier
//
//
//class MoviesViewModel : ViewModel() {
//    private val viewModelCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
//
//    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
//    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()
//
//    private val _isLoading = MutableStateFlow(true)
//    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
//
//    private val _selectedMovie = MutableStateFlow<Movie?>(null)
//    val selectedMovie: StateFlow<Movie?> = _selectedMovie.asStateFlow()
//
//    init {
//        fetchMovies()
//    }
//
//    private fun fetchMovies() {
//        viewModelScope.launch {
//            runCatching {
//                val movies = MovieRepository().getPopularMovies().results
//                _movies.value = movies
//                _isLoading.value = false
//            }.onFailure { e ->
//                if (e is Exception) {
//                    Napier.e(
//                        tag = "MovieRepository",
//                        message = "Error fetching movies: ${e.message}",
//                        throwable = e
//                    )
//                    _isLoading.value = false
//                }
//            }
//        }
//    }
//}
