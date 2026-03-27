package com.komig

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePNGRepresentation

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal actual class ImageCompressor actual constructor() {

    actual suspend fun compress(
        input: ByteArray,
        config: CompressionConfig,
    ): CompressionResult {
        val inputSize = input.size.toLong()
        val nsData = input.toNSData()

        // 1. Decode
        val uiImage = UIImage.imageWithData(nsData)
            ?: throw KomigException.DecodingException("UIImage failed to decode input data")

        val cgImage = uiImage.CGImage
            ?: throw KomigException.DecodingException("Failed to obtain CGImage from UIImage")

        val originalWidth = CGImageGetWidth(cgImage).toInt()
        val originalHeight = CGImageGetHeight(cgImage).toInt()

        // 2. Resize if needed
        val (targetW, targetH) = computeTargetSize(
            originalWidth, originalHeight, config.resizeMode,
        )

        val outputImage = if (targetW == originalWidth && targetH == originalHeight) {
            uiImage
        } else {
            resizeUIImage(uiImage, targetW, targetH)
                ?: throw KomigException.EncodingException("Failed to resize image")
        }

        // 3. Encode
        val outputData = encodeUIImage(outputImage, config.format, config.quality)
            ?: throw KomigException.EncodingException("Failed to encode image as ${config.format}")

        val outputBytes = outputData.toByteArray()
        return CompressionResult(
            bytes = outputBytes,
            width = targetW,
            height = targetH,
            format = config.format,
            inputSizeBytes = inputSize,
            outputSizeBytes = outputBytes.size.toLong(),
        )
    }

    private fun computeTargetSize(
        width: Int,
        height: Int,
        resizeMode: ResizeMode?,
    ): Pair<Int, Int> = when (resizeMode) {
        is ResizeMode.FitInside -> {
            if (width <= resizeMode.maxWidth && height <= resizeMode.maxHeight) {
                width to height
            } else {
                val ratio = minOf(
                    resizeMode.maxWidth.toFloat() / width,
                    resizeMode.maxHeight.toFloat() / height,
                )
                (width * ratio).toInt().coerceAtLeast(1) to
                    (height * ratio).toInt().coerceAtLeast(1)
            }
        }
        is ResizeMode.ExactResize -> resizeMode.width to resizeMode.height
        is ResizeMode.Percentage -> {
            (width * resizeMode.factor).toInt().coerceAtLeast(1) to
                (height * resizeMode.factor).toInt().coerceAtLeast(1)
        }
        null -> width to height
    }

    private fun resizeUIImage(image: UIImage, targetWidth: Int, targetHeight: Int): UIImage? {
        val size = CGSizeMake(
            targetWidth.toDouble(),
            targetHeight.toDouble(),
        )
        UIGraphicsBeginImageContextWithOptions(size, false, 1.0)
        image.drawInRect(CGRectMake(0.0, 0.0, targetWidth.toDouble(), targetHeight.toDouble()))
        val resized = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return resized
    }

    private fun encodeUIImage(image: UIImage, format: OutputFormat, quality: Int): NSData? {
        val compressionQuality = quality / 100.0
        return when (format) {
            OutputFormat.JPEG, OutputFormat.AUTO -> {
                UIImageJPEGRepresentation(image, compressionQuality)
            }
            OutputFormat.PNG -> {
                UIImagePNGRepresentation(image)
            }
            OutputFormat.WEBP -> {
                // iOS does not have a built-in UIImage → WebP encoder.
                // Fall back to JPEG encoding for WebP requests.
                // A future version could integrate a WebP encoder library.
                UIImageJPEGRepresentation(image, compressionQuality)
            }
        }
    }

    private fun ByteArray.toNSData(): NSData = usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
    }

    private fun NSData.toByteArray(): ByteArray {
        val length = this.length.toInt()
        if (length == 0) return byteArrayOf()
        val bytes = ByteArray(length)
        bytes.usePinned { pinned ->
            platform.posix.memcpy(pinned.addressOf(0), this.bytes, this.length)
        }
        return bytes
    }
}
