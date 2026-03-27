package com.komig

/**
 * Detects image format from magic bytes at the start of a byte array.
 *
 * - JPEG: starts with `FF D8 FF`
 * - PNG:  starts with `89 50 4E 47` (`‰PNG`)
 * - WebP: starts with `RIFF` (4 bytes), then 4 bytes of size, then `WEBP`
 */
internal object FormatDetector {

    fun detect(data: ByteArray): OutputFormat? = when {
        isJpeg(data) -> OutputFormat.JPEG
        isPng(data) -> OutputFormat.PNG
        isWebP(data) -> OutputFormat.WEBP
        else -> null
    }

    private fun isJpeg(data: ByteArray): Boolean =
        data.size >= 3 &&
            data[0] == 0xFF.toByte() &&
            data[1] == 0xD8.toByte() &&
            data[2] == 0xFF.toByte()

    private fun isPng(data: ByteArray): Boolean =
        data.size >= 4 &&
            data[0] == 0x89.toByte() &&
            data[1] == 0x50.toByte() &&
            data[2] == 0x4E.toByte() &&
            data[3] == 0x47.toByte()

    private fun isWebP(data: ByteArray): Boolean =
        data.size >= 12 &&
            data[0] == 0x52.toByte() && // R
            data[1] == 0x49.toByte() && // I
            data[2] == 0x46.toByte() && // F
            data[3] == 0x46.toByte() && // F
            data[8] == 0x57.toByte() && // W
            data[9] == 0x45.toByte() && // E
            data[10] == 0x42.toByte() && // B
            data[11] == 0x50.toByte() // P
}
