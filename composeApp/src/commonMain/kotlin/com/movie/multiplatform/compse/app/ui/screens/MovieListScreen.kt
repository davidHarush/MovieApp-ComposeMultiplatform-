package com.movie.multiplatform.compse.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.cash.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import com.movie.multiplatform.compse.app.AppScreen
import com.movie.multiplatform.compse.app.AppScreen.Companion.createRoute
import com.movie.multiplatform.compse.app.network.Movie
import com.movie.multiplatform.compse.app.network.MovieHelper


@Composable
fun MovieListScreen(movies: LazyPagingItems<Movie>, navController: NavController) {


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        if (movies.itemCount > 0) {
            items(movies.itemCount) { index ->
                MovieItem(index = index, movie = movies[index]!!, onMovieClick = {
                    navController.navigate(AppScreen.MovieDetails.createRoute(it.id.toString()))
                })
            }
        }

        movies.apply {
            when {
                loadState.append is androidx.paging.LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }

                loadState.refresh is androidx.paging.LoadState.Error -> {
                    item {
                        Text(
                            text = "Error loading movies",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MovieItem(index: Int, movie: Movie, onMovieClick: (Movie) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onMovieClick(movie) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = MovieHelper.getImage(movie.poster_path),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(180.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.h5,
                maxLines = 1,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movie.overview,
                style = MaterialTheme.typography.body2,
                color = Color.Gray,
                maxLines = 3,
                modifier = Modifier.padding(end = 8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
