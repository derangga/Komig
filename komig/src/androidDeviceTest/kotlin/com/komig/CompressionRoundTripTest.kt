package com.komig

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CompressionRoundTripTest {

    // --- JPEG round-trip ---

    @Test
    fun jpegRoundTrip() = runTest {
        val input = AndroidTestImages.createJpeg()
        val result = Komig.compress(input) {
            quality(80)
            format(OutputFormat.JPEG)
        }
        assertTrue(result.bytes.size > 2)
        assertEquals(0xFF.toByte(), result.bytes[0])
        assertEquals(0xD8.toByte(), result.bytes[1])
        assertEquals(OutputFormat.JPEG, result.format)
        assertEquals(10, result.width)
        assertEquals(10, result.height)
    }

    // --- PNG round-trip ---

    @Test
    fun pngRoundTrip() = runTest {
        val input = AndroidTestImages.createPng()
        val result = Komig.compress(input) {
            quality(80)
            format(OutputFormat.PNG)
        }
        assertTrue(result.bytes.size > 4)
        assertEquals(0x89.toByte(), result.bytes[0])
        assertEquals(0x50.toByte(), result.bytes[1])
        assertEquals(OutputFormat.PNG, result.format)
    }

    // --- WebP round-trip ---

    @Test
    fun webpRoundTrip() = runTest {
        val input = AndroidTestImages.createWebP()
        val result = Komig.compress(input) {
            quality(80)
            format(OutputFormat.WEBP)
        }
        assertTrue(result.bytes.isNotEmpty())
        assertEquals(OutputFormat.WEBP, result.format)
    }

    // --- AUTO format preserves detected format ---

    @Test
    fun autoFormatPreservesJpeg() = runTest {
        val input = AndroidTestImages.createJpeg()
        val result = Komig.compress(input) {
            quality(80)
        }
        assertEquals(OutputFormat.JPEG, result.format)
        assertEquals(0xFF.toByte(), result.bytes[0])
        assertEquals(0xD8.toByte(), result.bytes[1])
    }

    @Test
    fun autoFormatPreservesPng() = runTest {
        val input = AndroidTestImages.createPng()
        val result = Komig.compress(input) {
            quality(80)
        }
        assertEquals(OutputFormat.PNG, result.format)
        assertEquals(0x89.toByte(), result.bytes[0])
    }

    // --- Quality affects output size ---

    @Test
    fun lowerQualityProducesSmallerOutput() = runTest {
        val input = AndroidTestImages.createJpeg(100, 100)
        val highQuality = Komig.compress(input) {
            quality(100)
            format(OutputFormat.JPEG)
        }
        val lowQuality = Komig.compress(input) {
            quality(1)
            format(OutputFormat.JPEG)
        }
        assertTrue(lowQuality.outputSizeBytes <= highQuality.outputSizeBytes)
    }

    // --- Format conversion ---

    @Test
    fun jpegToPngConversion() = runTest {
        val input = AndroidTestImages.createJpeg()
        val result = Komig.compress(input) {
            format(OutputFormat.PNG)
        }
        assertEquals(OutputFormat.PNG, result.format)
        assertEquals(0x89.toByte(), result.bytes[0])
        assertEquals(0x50.toByte(), result.bytes[1])
    }

    @Test
    fun pngToJpegConversion() = runTest {
        val input = AndroidTestImages.createPng()
        val result = Komig.compress(input) {
            format(OutputFormat.JPEG)
        }
        assertEquals(OutputFormat.JPEG, result.format)
        assertEquals(0xFF.toByte(), result.bytes[0])
        assertEquals(0xD8.toByte(), result.bytes[1])
    }

    // --- Resize modes ---

    @Test
    fun fitInsideResize() = runTest {
        val input = AndroidTestImages.createJpeg(200, 100)
        val result = Komig.compress(input) {
            format(OutputFormat.JPEG)
            maxResolution(50, 50)
        }
        assertTrue(result.width <= 50)
        assertTrue(result.height <= 50)
    }

    @Test
    fun exactResize() = runTest {
        val input = AndroidTestImages.createJpeg(100, 100)
        val result = Komig.compress(input) {
            format(OutputFormat.JPEG)
            resize(ResizeMode.ExactResize(30, 20))
        }
        assertEquals(30, result.width)
        assertEquals(20, result.height)
    }

    @Test
    fun percentageResize() = runTest {
        val input = AndroidTestImages.createJpeg(100, 100)
        val result = Komig.compress(input) {
            format(OutputFormat.JPEG)
            resize(ResizeMode.Percentage(0.5f))
        }
        assertEquals(50, result.width)
        assertEquals(50, result.height)
    }

    // --- CompressionResult fields ---

    @Test
    fun resultContainsInputAndOutputSize() = runTest {
        val input = AndroidTestImages.createJpeg()
        val result = Komig.compress(input) {
            format(OutputFormat.JPEG)
        }
        assertEquals(input.size.toLong(), result.inputSizeBytes)
        assertEquals(result.bytes.size.toLong(), result.outputSizeBytes)
    }
}
