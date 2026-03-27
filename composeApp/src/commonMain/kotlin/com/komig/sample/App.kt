package com.komig.sample

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import komigsample.composeapp.generated.resources.Res
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize().safeContentPadding(),
            color = MaterialTheme.colorScheme.background,
        ) {
            val viewModel = viewModel { CompressViewModel() }
            val state by viewModel.state.collectAsState()
            val picker = rememberImagePickerLauncher { bytes ->
                if (bytes != null) {
                    viewModel.onImagePicked(bytes)
                }
            }
            val scope = rememberCoroutineScope()
            val saver = rememberImageSaverLauncher { /* saved */ }

            CompressScreen(
                state = state,
                onPickImage = { picker.launch() },
                onUseDemoImage = {
                    scope.launch {
                        val bytes = Res.readBytes("drawable/frieren.jpg")
                        viewModel.onImagePicked(bytes)
                    }
                },
                onCompress = { viewModel.compress() },
                onSaveImage = {
                    val result = state.result
                    if (result != null) {
                        saver.save(result.bytes, result.format)
                    }
                },
                onQualityChanged = { viewModel.onQualityChanged(it) },
                onFormatChanged = { viewModel.onFormatChanged(it) },
                onMaxResolutionChanged = { w, h -> viewModel.onMaxResolutionChanged(w, h) },
            )
        }
    }
}
