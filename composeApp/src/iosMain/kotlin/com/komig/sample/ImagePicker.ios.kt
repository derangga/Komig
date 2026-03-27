package com.komig.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import platform.Foundation.NSData
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerConfigurationSelectionOrdered
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.darwin.NSObject
import platform.posix.memcpy

actual class ImagePickerLauncher(
    private val onLaunch: () -> Unit,
) {
    actual fun launch() {
        onLaunch()
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    val bytes = ByteArray(size)
    if (size > 0) {
        memcpy(bytes.refTo(0), this.bytes, this.length)
    }
    return bytes
}

@Composable
actual fun rememberImagePickerLauncher(onResult: (ByteArray?) -> Unit): ImagePickerLauncher {
    return remember {
        ImagePickerLauncher {
            val configuration = PHPickerConfiguration().apply {
                selectionLimit = 1
                selection = PHPickerConfigurationSelectionOrdered
                filter = PHPickerFilter.imagesFilter
            }
            val picker = PHPickerViewController(configuration = configuration)
            val delegate = object : NSObject(), PHPickerViewControllerDelegateProtocol {
                override fun picker(
                    picker: PHPickerViewController,
                    didFinishPicking: List<*>,
                ) {
                    picker.dismissViewControllerAnimated(true, null)
                    val result = didFinishPicking.firstOrNull() as? PHPickerResult
                    if (result == null) {
                        onResult(null)
                        return
                    }
                    result.itemProvider.loadDataRepresentationForTypeIdentifier(
                        typeIdentifier = "public.image",
                    ) { data, _ ->
                        if (data != null) {
                            val image = UIImage(data = data)
                            val jpegData = UIImageJPEGRepresentation(image, 1.0)
                            onResult(jpegData?.toByteArray())
                        } else {
                            onResult(null)
                        }
                    }
                }
            }
            picker.delegate = delegate
            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            rootViewController?.presentViewController(picker, animated = true, completion = null)
        }
    }
}
