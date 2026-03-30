package com.komig

/** Platform-specific file I/O for reading and writing image bytes. */
internal expect object FileHandler {
    /** Read all bytes from the file at [filePath]. */
    fun readBytes(filePath: String): ByteArray

    /**
     * Write [bytes] to a file at [directory]/[fileName].
     * Creates parent directories if they don't exist.
     *
     * @return the absolute path of the written file.
     */
    fun writeBytes(bytes: ByteArray, directory: String, fileName: String): String
}
