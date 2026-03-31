package com.komig

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

internal actual object FileHandler {
    actual fun readBytes(filePath: String): ByteArray {
        try {
            return File(filePath).readBytes()
        } catch (e: IOException) {
            throw KomigException.FileIOException("Failed to read file: $filePath", e)
        }
    }

    actual fun writeBytes(bytes: ByteArray, directory: String, fileName: String): String {
        try {
            val dir = File(directory)
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            FileOutputStream(file).use { it.write(bytes) }
            return file.absolutePath
        } catch (e: IOException) {
            throw KomigException.FileIOException("Failed to write file: $directory/$fileName", e)
        }
    }
}
