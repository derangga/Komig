package com.komig

/** Platform-specific image compression engine. */
internal expect class ImageCompressor() {

    /** Compress [input] bytes according to [config], returning the result. */
    suspend fun compress(input: ByteArray, config: CompressionConfig): CompressionResult
}
