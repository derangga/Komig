package com.komig.sample

import androidx.compose.runtime.Composable
import com.komig.OutputFormat

expect class ImageSaverLauncher {
    fun save(bytes: ByteArray, format: OutputFormat)
}

@Composable
expect fun rememberImageSaverLauncher(onResult: (Boolean) -> Unit): ImageSaverLauncher
