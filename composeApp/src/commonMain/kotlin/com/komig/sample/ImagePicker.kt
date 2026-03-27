package com.komig.sample

import androidx.compose.runtime.Composable

expect class ImagePickerLauncher {
    fun launch()
}

@Composable
expect fun rememberImagePickerLauncher(onResult: (ByteArray?) -> Unit): ImagePickerLauncher
