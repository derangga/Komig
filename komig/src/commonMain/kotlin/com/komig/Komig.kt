package com.komig

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Entry point for the Komig image compression library.
 *
 * ```kotlin
 * val result = Komig.compress(imageBytes) {
 *     quality(80)
 *     format(OutputFormat.WEBP)
 *     maxResolution(1920, 1080)
 * }
 * ```
 */
object Komig {

    /**
     * Compress [input] image bytes using the configuration built by [block].
     *
     * When the format is [OutputFormat.AUTO], the input format is detected
     * from magic bytes and preserved. Falls back to JPEG if detection fails.
     *
     * @throws KomigException on invalid config, unsupported format, or codec failure.
     */
    suspend fun compress(
        input: ByteArray,
        block: CompressionConfig.Builder.() -> Unit = {},
    ): CompressionResult {
        val config = CompressionConfig.Builder().apply(block).build()
        val resolved = resolveFormat(input, config)
        return withContext(Dispatchers.Default) {
            ImageCompressor().compress(input, resolved)
        }
    }

    private fun resolveFormat(
        input: ByteArray,
        config: CompressionConfig,
    ): CompressionConfig {
        if (config.format != OutputFormat.AUTO) return config

        val detected = FormatDetector.detect(input)
            ?: throw KomigException.UnsupportedFormatException(
                "Unable to detect image format from input bytes",
            )
        return CompressionConfig.Builder().apply {
            quality(config.quality)
            format(detected)
            config.resizeMode?.let { resize(it) }
        }.build()
    }
}
