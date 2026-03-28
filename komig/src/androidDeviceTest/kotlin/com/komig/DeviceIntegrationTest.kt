package com.komig

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DeviceIntegrationTest {

    @Test
    fun compressLargeJpegWithFitInside() = runTest {
        val input = AndroidTestImages.createLargeJpeg(1920, 1080)
        val result = Komig.compress(input) {
            quality(80)
            maxResolution(800, 600)
        }
        assertTrue(result.width <= 800)
        assertTrue(result.height <= 600)
        assertTrue(result.outputSizeBytes > 0)
        assertTrue(result.outputSizeBytes < result.inputSizeBytes)
    }

    @Test
    fun corruptDataThrowsDecodingException() = runTest {
        val corrupt = byteArrayOf(
            0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(),
            0x00, 0x01, 0x02, 0x03,
        )
        assertFailsWith<KomigException.DecodingException> {
            Komig.compress(corrupt) {
                format(OutputFormat.JPEG)
            }
        }
    }

    @Test
    fun explicitFormatSkipsDetection() = runTest {
        assertFailsWith<KomigException.DecodingException> {
            Komig.compress(TestFixtures.UNKNOWN_FORMAT) {
                format(OutputFormat.JPEG)
            }
        }
    }

    @Test
    fun qualityOneProducesOutput() = runTest {
        val input = AndroidTestImages.createJpeg(100, 100)
        val result = Komig.compress(input) {
            quality(1)
            format(OutputFormat.JPEG)
        }
        assertTrue(result.bytes.isNotEmpty())
    }

    @Test
    fun quality100ProducesOutput() = runTest {
        val input = AndroidTestImages.createJpeg(100, 100)
        val result = Komig.compress(input) {
            quality(100)
            format(OutputFormat.JPEG)
        }
        assertTrue(result.bytes.isNotEmpty())
    }
}
