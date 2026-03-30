package com.komig

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.writeToFile

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal actual object FileHandler {
    actual fun readBytes(filePath: String): ByteArray {
        val nsData = NSData.dataWithContentsOfFile(filePath)
            ?: throw KomigException.FileIOException("Failed to read file: $filePath")
        return nsData.toByteArray()
    }

    actual fun writeBytes(bytes: ByteArray, directory: String, fileName: String): String {
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(directory)) {
            val created = fileManager.createDirectoryAtPath(
                path = directory,
                withIntermediateDirectories = true,
                attributes = null,
                error = null,
            )
            if (!created) {
                throw KomigException.FileIOException("Failed to create directory: $directory")
            }
        }

        val filePath = "$directory/$fileName"
        val nsData = bytes.toNSData()
        val success = nsData.writeToFile(filePath, atomically = true)
        if (!success) {
            throw KomigException.FileIOException("Failed to write file: $filePath")
        }
        return filePath
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

    private fun ByteArray.toNSData(): NSData = usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
    }
}
