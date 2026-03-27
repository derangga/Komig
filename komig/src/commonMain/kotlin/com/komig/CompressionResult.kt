package com.komig

/** Result returned after a successful image compression. */
data class CompressionResult(
    val bytes: ByteArray,
    val width: Int,
    val height: Int,
    val format: OutputFormat,
    val inputSizeBytes: Long,
    val outputSizeBytes: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CompressionResult) return false
        return bytes.contentEquals(other.bytes) &&
            width == other.width &&
            height == other.height &&
            format == other.format &&
            inputSizeBytes == other.inputSizeBytes &&
            outputSizeBytes == other.outputSizeBytes
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + format.hashCode()
        result = 31 * result + inputSizeBytes.hashCode()
        result = 31 * result + outputSizeBytes.hashCode()
        return result
    }
}
