package com.komig

/** Result returned after a successful image compression. */
class CompressionResult(
    bytes: ByteArray,
    val width: Int,
    val height: Int,
    val format: OutputFormat,
    val inputSizeBytes: Long,
    val outputSizeBytes: Long,
) {
    private var _bytes: ByteArray? = bytes
    private var _cachedPath: String? = null

    /**
     * The compressed image bytes.
     *
     * @throws IllegalStateException if [cacheResult] has been called and bytes were released.
     */
    val bytes: ByteArray
        get() = _bytes ?: throw IllegalStateException(
            "Bytes have been released after caching. Use the cached file at: $_cachedPath"
        )

    /** The file path of the cached result, or `null` if [cacheResult] has not been called. */
    val cachedPath: String? get() = _cachedPath

    /** Whether the in-memory bytes are still available. */
    val isBytesAvailable: Boolean get() = _bytes != null

    /**
     * Cache the compressed bytes to disk and release them from memory.
     *
     * After calling this method, accessing [bytes] will throw [IllegalStateException].
     * Use [cachedPath] to retrieve the file location.
     *
     * @param directory the directory to write the cached file into.
     * @param fileName the file name **without extension**. The extension is derived from [format].
     * @return the absolute path of the cached file.
     * @throws KomigException.FileIOException if writing to disk fails.
     * @throws IllegalStateException if this result has already been cached.
     */
    fun cacheResult(directory: String, fileName: String): String {
        check(_cachedPath == null) { "Result has already been cached at: $_cachedPath" }
        val currentBytes = bytes // throws if already released
        val fullFileName = "$fileName.${format.extension}"
        val path = FileHandler.writeBytes(currentBytes, directory, fullFileName)
        _cachedPath = path
        _bytes = null
        return path
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CompressionResult) return false
        return _bytes.contentEquals(other._bytes) &&
            width == other.width &&
            height == other.height &&
            format == other.format &&
            inputSizeBytes == other.inputSizeBytes &&
            outputSizeBytes == other.outputSizeBytes
    }

    override fun hashCode(): Int {
        var result = _bytes.contentHashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + format.hashCode()
        result = 31 * result + inputSizeBytes.hashCode()
        result = 31 * result + outputSizeBytes.hashCode()
        return result
    }
}
