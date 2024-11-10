package com.movie.multiplatform.compse.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.movie.multiplatform.compse.app.network.CastMember
import com.movie.multiplatform.compse.app.network.MovieCollectionResponse
import com.movie.multiplatform.compse.app.network.MovieDetails
import com.movie.multiplatform.compse.app.network.MovieHelper
import com.movie.multiplatform.compse.app.network.MovieImagesResponse
import com.movie.multiplatform.compse.app.network.MovieRepository


@Composable
fun MovieDetailsScreen(movieId: Int, onBack: () -> Unit) {
    val repository = MovieRepository()
    var movieDetails by remember { mutableStateOf<MovieDetails?>(null) }
    var castList by remember { mutableStateOf<List<CastMember>>(emptyList()) }
    var movieImages by remember{ mutableStateOf<MovieImagesResponse?>(null) }
    var collection by remember { mutableStateOf<MovieCollectionResponse?>(null)}


    LaunchedEffect(movieId) {
        movieDetails = repository.getMovieDetails(movieId)
        movieImages = repository.getMovieImages(movieId)
        castList = repository.getMovieCast(movieId)
        castList = castList.filter { it.profile_path != null }

        movieDetails?.belongs_to_collection.let {
            collection = repository.getCollectionDetails(it?.id ?: 0)
        }
    }

    val gradientColors = listOf(MaterialTheme.colors.primary, Color.Transparent)



    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .zIndex(1f),
        contentAlignment = Alignment.TopStart
    ) {
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White.copy(alpha = 0.8f)),
            contentPadding = PaddingValues(8.dp),
            shape = CircleShape
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 55.dp)
            .background(Brush.verticalGradient(gradientColors))
    ) {
        movieDetails?.let { details ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp, horizontal = 12.dp)
            ) {
                MainImage( details.backdrop_path)
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = details.title,
                        style = MaterialTheme.typography.h4.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colors.onSurface
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Release Date: ${details.release_date}",
                        style = MaterialTheme.typography.subtitle2,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = details.overview ?: "",
                        style = MaterialTheme.typography.body1.copy(lineHeight = 24.sp),
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Cast",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.SemiBold

                )
                CastList(castList)


                movieImages?.let { images ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Movie Posters",
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.SemiBold
                    )
                    MovieImagesList(images)
                }

                if( collection != null && collection?.parts?.isNotEmpty() == true)
                {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Collection: ${collection?.name}",
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = collection?.overview ?: "",
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Normal,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(8.dp)
                    )
                    Collection(collection!!)

                }
            }
        }
    }
}


@Composable
private fun MovieImagesList(movieImages: MovieImagesResponse) {

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(movieImages.posters.size.coerceAtMost(8)) { index ->
            AsyncImage(
                model = MovieHelper.getImage(movieImages.posters[index].file_path),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .graphicsLayer {
                        shadowElevation = 8.dp.toPx()
                        shape = RoundedCornerShape(16.dp)
                        clip = true
                    }
            )
        }
    }


}

@Composable
private fun MainImage(backdropPath: String?) =
    backdropPath?.let {
        AsyncImage(
            model = MovieHelper.getImage(backdropPath),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp))
                .graphicsLayer {
                    shadowElevation = 8.dp.toPx()
                    shape = RoundedCornerShape(16.dp)
                    clip = true
                }
        )
    }

@Composable
private fun CastList(castList: List<CastMember>)  =
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(castList.size) { index ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(100.dp)
                    .padding(vertical = 8.dp)
            ) {
                AsyncImage(
                    model = MovieHelper.getImage(castList[index].profile_path ?: ""),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                Text(
                    text = castList[index].name,
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = castList[index].character,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

@Composable
private fun Collection(collection: MovieCollectionResponse) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(collection.parts?.size ?: 0) { index ->
            if(collection.parts?.get(index)?.backdrop_path != null)
                Column {

                    if(collection.parts[index].backdrop_path == null) return@items

                    AsyncImage(
                        model = MovieHelper.getImage(
                            collection.parts[index].backdrop_path ?: ""
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(180.dp)
                            .height(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .graphicsLayer {
                                shadowElevation = 8.dp.toPx()
                                shape = RoundedCornerShape(16.dp)
                                clip = true
                            }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = collection.parts[index].title,
                        style = MaterialTheme.typography.subtitle2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
        }
    }
}

@Composable
private fun BackButton(onBack: () -> Unit) =
    Button(
        onClick = onBack,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .height(40.dp)
            .graphicsLayer {
                shape = RoundedCornerShape(16)
            },
    ) {
        Text("Back", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.button)
    }