package com.komig

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FormatDetectorTest {

    @Test
    fun detectJpeg() {
        assertEquals(OutputFormat.JPEG, FormatDetector.detect(TestFixtures.MINIMAL_JPEG))
    }

    @Test
    fun detectPng() {
        assertEquals(OutputFormat.PNG, FormatDetector.detect(TestFixtures.MINIMAL_PNG))
    }

    @Test
    fun detectWebP() {
        assertEquals(OutputFormat.WEBP, FormatDetector.detect(TestFixtures.MINIMAL_WEBP))
    }

    @Test
    fun unknownFormatReturnsNull() {
        assertNull(FormatDetector.detect(TestFixtures.UNKNOWN_FORMAT))
    }

    @Test
    fun emptyBytesReturnsNull() {
        assertNull(FormatDetector.detect(TestFixtures.EMPTY))
    }

    @Test
    fun tooShortBytesReturnsNull() {
        assertNull(FormatDetector.detect(TestFixtures.TOO_SHORT))
    }

    @Test
    fun corruptJpegHeaderReturnsNull() {
        assertNull(FormatDetector.detect(TestFixtures.CORRUPT_JPEG_HEADER))
    }

    @Test
    fun corruptPngHeaderReturnsNull() {
        assertNull(FormatDetector.detect(TestFixtures.CORRUPT_PNG_HEADER))
    }

    @Test
    fun corruptWebPHeaderReturnsNull() {
        assertNull(FormatDetector.detect(TestFixtures.CORRUPT_WEBP_HEADER))
    }
}
