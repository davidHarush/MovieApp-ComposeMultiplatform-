package com.movie.multiplatform.compse.app.imageSelection

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.preat.peekaboo.image.picker.ResizeOptions
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap


@Composable
fun ImageSelectionSingle() {
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
fun ImageSelectionMultiple() {
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
            Text("Pick Multiple Images")
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

