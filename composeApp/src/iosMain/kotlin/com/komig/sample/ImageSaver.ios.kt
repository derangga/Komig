package com.komig.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.komig.OutputFormat
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual class ImageSaverLauncher(
    private val onSave: (ByteArray, OutputFormat) -> Unit,
) {
    actual fun save(bytes: ByteArray, format: OutputFormat) {
        onSave(bytes, format)
    }
}

private fun extensionFor(format: OutputFormat): String = when (format) {
    OutputFormat.PNG -> "png"
    OutputFormat.WEBP -> "webp"
    else -> "jpg"
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun rememberImageSaverLauncher(onResult: (Boolean) -> Unit): ImageSaverLauncher {
    return remember {
        ImageSaverLauncher { bytes, format ->
            try {
                val nsData = bytes.usePinned { pinned ->
                    NSData.create(
                        bytes = pinned.addressOf(0),
                        length = bytes.size.toULong(),
                    )
                }
                // Write to a temp file in Documents directory
                val fileManager = NSFileManager.defaultManager
                val docsUrl = fileManager.URLsForDirectory(
                    NSDocumentDirectory,
                    NSUserDomainMask,
                ).firstOrNull() as? NSURL

                val fileName = "compressed_image.${extensionFor(format)}"
                val fileUrl = docsUrl?.let { NSURL.fileURLWithPath("${it.path}/$fileName") }

                if (fileUrl != null && nsData.writeToURL(fileUrl, atomically = true)) {
                    // Present share sheet so user can choose where to save
                    val activityVC = UIActivityViewController(
                        activityItems = listOf(fileUrl),
                        applicationActivities = null,
                    )
                    val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
                    rootVC?.presentViewController(activityVC, animated = true, completion = null)
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (_: Exception) {
                onResult(false)
            }
        }
    }
}
