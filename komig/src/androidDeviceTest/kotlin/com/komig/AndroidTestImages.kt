package com.komig

import android.graphics.Bitmap
import android.graphics.Color
import java.io.ByteArrayOutputStream

/**
 * Generates real encoded images on-device using Android's Bitmap API.
 * These are valid images that BitmapFactory can decode, unlike the
 * minimal hand-crafted byte arrays in TestFixtures.
 */
object AndroidTestImages {

    fun createJpeg(width: Int = 10, height: Int = 10, quality: Int = 90): ByteArray {
        return encode(createBitmap(width, height), Bitmap.CompressFormat.JPEG, quality)
    }

    fun createPng(width: Int = 10, height: Int = 10): ByteArray {
        return encode(createBitmap(width, height), Bitmap.CompressFormat.PNG, 100)
    }

    fun createWebP(width: Int = 10, height: Int = 10, quality: Int = 90): ByteArray {
        val format = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            @Suppress("DEPRECATION")
            Bitmap.CompressFormat.WEBP
        }
        return encode(createBitmap(width, height), format, quality)
    }

    fun createLargeJpeg(width: Int = 1920, height: Int = 1080): ByteArray {
        return createJpeg(width, height)
    }

    private fun createBitmap(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.RED)
        return bitmap
    }

    private fun encode(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(format, quality, stream)
        bitmap.recycle()
        return stream.toByteArray()
    }
}
