package com.komig.sample

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

actual class ImagePickerLauncher(
    private val launcher: () -> Unit,
) {
    actual fun launch() {
        launcher()
    }
}

@Composable
actual fun rememberImagePickerLauncher(onResult: (ByteArray?) -> Unit): ImagePickerLauncher {
    val context = LocalContext.current
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            onResult(bytes)
        } else {
            onResult(null)
        }
    }
    return remember(pickMedia) {
        ImagePickerLauncher {
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}
