package com.komig.sample

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.komig.OutputFormat

actual class ImageSaverLauncher(
    private val onSave: (ByteArray, OutputFormat) -> Unit,
) {
    actual fun save(bytes: ByteArray, format: OutputFormat) {
        onSave(bytes, format)
    }
}

private fun mimeTypeFor(format: OutputFormat): String = when (format) {
    OutputFormat.PNG -> "image/png"
    OutputFormat.WEBP -> "image/webp"
    else -> "image/jpeg"
}

private fun extensionFor(format: OutputFormat): String = when (format) {
    OutputFormat.PNG -> "png"
    OutputFormat.WEBP -> "webp"
    else -> "jpg"
}

@Composable
actual fun rememberImageSaverLauncher(onResult: (Boolean) -> Unit): ImageSaverLauncher {
    val context = LocalContext.current
    var pendingBytes by remember { mutableStateOf<ByteArray?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/*"),
    ) { uri ->
        val bytes = pendingBytes
        if (uri != null && bytes != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
                onResult(true)
            } catch (_: Exception) {
                onResult(false)
            }
        } else {
            onResult(false)
        }
        pendingBytes = null
    }

    return remember(launcher) {
        ImageSaverLauncher { bytes, format ->
            pendingBytes = bytes
            launcher.launch("compressed_image.${extensionFor(format)}")
        }
    }
}
