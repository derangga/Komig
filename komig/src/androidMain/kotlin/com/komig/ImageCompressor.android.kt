package com.komig

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream

internal actual class ImageCompressor actual constructor() {

    actual suspend fun compress(
        input: ByteArray,
        config: CompressionConfig,
    ): CompressionResult {
        val inputSize = input.size.toLong()

        // 1. Read original dimensions without allocating pixels
        val bounds = decodeBounds(input)
        val originalWidth = bounds.outWidth
        val originalHeight = bounds.outHeight

        if (originalWidth <= 0 || originalHeight <= 0) {
            throw KomigException.DecodingException(
                "Failed to read image dimensions (${bounds.outWidth}x${bounds.outHeight})",
            )
        }

        // 2. Calculate subsample size if FitInside is requested
        val sampleSize = computeSampleSize(originalWidth, originalHeight, config.resizeMode)

        // 3. Decode with subsampling
        val decoded = decodeBitmap(input, sampleSize)
            ?: throw KomigException.DecodingException("BitmapFactory returned null")

        return decoded.useAndRecycle { source ->
            // 4. Resize if needed
            val resized = applyResize(source, originalWidth, originalHeight, config.resizeMode)
            val shouldRecycleResized = resized !== source

            resized.useAndRecycle(recycle = shouldRecycleResized) { bitmap ->
                // 5. Encode
                val compressFormat = mapFormat(config.format)
                val outputBytes = encode(bitmap, compressFormat, config.quality)

                CompressionResult(
                    bytes = outputBytes,
                    width = bitmap.width,
                    height = bitmap.height,
                    format = config.format,
                    inputSizeBytes = inputSize,
                    outputSizeBytes = outputBytes.size.toLong(),
                )
            }
        }
    }

    private fun decodeBounds(input: ByteArray): BitmapFactory.Options {
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(input, 0, input.size, opts)
        return opts
    }

    private fun computeSampleSize(
        originalWidth: Int,
        originalHeight: Int,
        resizeMode: ResizeMode?,
    ): Int {
        val fitInside = resizeMode as? ResizeMode.FitInside ?: return 1
        var sample = 1
        var w = originalWidth
        var h = originalHeight
        while (w / 2 >= fitInside.maxWidth && h / 2 >= fitInside.maxHeight) {
            sample *= 2
            w /= 2
            h /= 2
        }
        return sample
    }

    private fun decodeBitmap(input: ByteArray, sampleSize: Int): Bitmap? {
        val opts = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        return BitmapFactory.decodeByteArray(input, 0, input.size, opts)
    }

    private fun applyResize(
        bitmap: Bitmap,
        originalWidth: Int,
        originalHeight: Int,
        resizeMode: ResizeMode?,
    ): Bitmap = when (resizeMode) {
        is ResizeMode.FitInside -> {
            val (targetW, targetH) = fitInsideDimensions(
                bitmap.width, bitmap.height,
                resizeMode.maxWidth, resizeMode.maxHeight,
            )
            if (targetW == bitmap.width && targetH == bitmap.height) {
                bitmap
            } else {
                bitmap.scale(targetW, targetH)
            }
        }

        is ResizeMode.ExactResize -> {
            bitmap.scale(resizeMode.width, resizeMode.height)
        }

        is ResizeMode.Percentage -> {
            val targetW = (originalWidth * resizeMode.factor).toInt().coerceAtLeast(1)
            val targetH = (originalHeight * resizeMode.factor).toInt().coerceAtLeast(1)
            bitmap.scale(targetW, targetH)
        }

        null -> bitmap
    }

    private fun fitInsideDimensions(
        currentW: Int,
        currentH: Int,
        maxW: Int,
        maxH: Int,
    ): Pair<Int, Int> {
        if (currentW <= maxW && currentH <= maxH) return currentW to currentH
        val ratio = minOf(maxW.toFloat() / currentW, maxH.toFloat() / currentH)
        return (currentW * ratio).toInt().coerceAtLeast(1) to
            (currentH * ratio).toInt().coerceAtLeast(1)
    }

    private fun mapFormat(format: OutputFormat): Bitmap.CompressFormat = when (format) {
        OutputFormat.JPEG -> Bitmap.CompressFormat.JPEG
        OutputFormat.PNG -> Bitmap.CompressFormat.PNG
        OutputFormat.WEBP -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                @Suppress("DEPRECATION")
                Bitmap.CompressFormat.WEBP
            }
        }
        OutputFormat.AUTO -> Bitmap.CompressFormat.JPEG // should not reach here after resolve
    }

    private fun encode(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        quality: Int,
    ): ByteArray {
        val stream = ByteArrayOutputStream()
        val success = bitmap.compress(format, quality, stream)
        if (!success) {
            throw KomigException.EncodingException("Bitmap.compress returned false for $format")
        }
        return stream.toByteArray()
    }
}

private inline fun <R> Bitmap.useAndRecycle(recycle: Boolean = true, block: (Bitmap) -> R): R =
    try {
        block(this)
    } finally {
        if (recycle) recycle()
    }
