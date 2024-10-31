package com.movie.multiplatform.compse.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.Bitmap
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.movie.multiplatform.compse.app.network.Movie
import com.movie.multiplatform.compse.app.network.MovieHelper
import com.movie.multiplatform.compse.app.network.MovieRepository
import com.movie.multiplatform.compse.app.network.TelegramBotRepository
import com.preat.peekaboo.image.picker.ResizeOptions
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


fun getMovies(scope: CoroutineScope, onMoviesFetched: (List<Movie>) -> Unit) {
    scope.launch {
        runCatching {
            val movies = MovieRepository().getPopularMovies().results
            println("movies: $movies")
            onMoviesFetched(movies)
        }.onFailure { e ->
            if (e is Exception) {
                Napier.e(
                    tag = "MovieRepository",
                    message = "Error fetching movies: ${e.message}",
                    throwable = e
                )
                println("Handled error: ${e.message}")
            }
        }
    }
}
fun getAsyncImageLoader(context: PlatformContext)=
    ImageLoader.Builder(context).crossfade(true).logger(DebugLogger()).build()

@OptIn(ExperimentalCoilApi::class)
@Composable
fun App() {

    val scope = rememberCoroutineScope()
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var isLoading = remember { mutableStateOf(true) }
    var selectedMovie by remember { mutableStateOf<Movie?>(null) }



    LaunchedEffect(Unit) {
   //     TelegramBotRepository().sendMessageToChat("Hello from ${getPlatform().name}")

        getMovies(scope) { movie ->
            movies = movie.toMutableList()
            isLoading.value = false
        }
    }

    MaterialTheme {
        setSingletonImageLoaderFactory { context ->
            getAsyncImageLoader(context)
        }
        if (selectedMovie != null) {
            MovieDetailsScreen(movie = selectedMovie!!, onBack = { selectedMovie = null })
        } else {
            LazyColumn(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                if (isLoading.value) {
                    item {
                        Spacer(modifier = Modifier.padding(36.dp))
                        Text("Loading...")
                        CircularProgressIndicator()
                    }
                } else {
                    items(movies) { movie ->
                        MovieItem(movie, onMovieClick = { selectedMovie = it })
                        Divider()
                    }
                }
            }
        }
    }
}

//@Composable
//fun testTelegramBot() {
//    rememberCoroutineScope().launch {
//        TelegramBotRepository().sendMessageToChat("Hello from Compose  -> device: ${getPlatform().name}")
////            val stickerFileId =
////                "CAACAgQAAxkBAAEuxWJnI0kknwek2BEc-_ihqOvdJorztwACAQcAApWtnAForTr_KwnG6zYE"
////            sendStickerToChat(stickerFileId)
////            sendWelcomeMessageWithInlineKeyboard()
////            startListeningForCallbackQueries(this@launch)
//
//    }
//
//
//}

@Composable
fun MovieItem(movie: Movie, onMovieClick: (Movie) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onMovieClick(movie) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = MovieHelper.getImagesUrl(movie),
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

@Composable
fun CustomCameraView() {
    val state = rememberPeekabooCameraState(onCapture = { /* Handle captured images */ })
    PeekabooCamera(
        state = state,
        modifier = Modifier.fillMaxSize(),
        permissionDeniedContent = {
            // Custom UI content for permission denied scenario
        },
    )
}

@Composable
fun MovieDetailsScreen_X(movie: Movie, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()

    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                // Process the selected images' ByteArrays.
                println(it)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = movie.title,
            style = MaterialTheme.typography.h4,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        AsyncImage(
            model = MovieHelper.getImagesUrl(movie),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = movie.overview,
            style = MaterialTheme.typography.body1,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) {
            Text("Back")
        }



        Button(
            onClick = {
                singleImagePicker.launch()
            }
        ) {
            Text("Pick Single Image")
        }
    }
}


@Composable
fun MovieDetailsScreen(movie: Movie, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var selectedBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        resizeOptions = ResizeOptions(compressionQuality = 0.5),
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let { byteArray ->
                selectedBitmap = byteArray.toImageBitmap()
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                singleImagePicker.launch()
            }
        ) {
            Text("Pick Single Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = "Selected Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))

            )
        }
    }
}


@Composable
fun MovieDetailsScreen3(movie: Movie, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var selectedBitmap by remember { mutableStateOf<List<ImageBitmap?>>(emptyList()) }

    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Multiple(maxSelection = 3),
        scope = scope,
        onResult = { byteArrays ->

            byteArrays.firstOrNull()?.let { byteArray ->
                selectedBitmap = byteArrays.map { it.toImageBitmap() }
            }
        }


//            byteArrays.firstOrNull()?.let { byteArray ->
//                byteArrays
//                selectedBitmap = byteArray.toImageBitmap()
//            }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                singleImagePicker.launch()
            }
        ) {
            Text("Pick Single Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedBitmap.forEach {
            it?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = "Selected Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))

                )
            }
        }


    }
}

