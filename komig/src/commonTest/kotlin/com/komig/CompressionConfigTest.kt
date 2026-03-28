package com.komig

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class CompressionConfigTest {

    private fun buildConfig(block: CompressionConfig.Builder.() -> Unit = {}): CompressionConfig {
        return CompressionConfig.Builder().apply(block).build()
    }

    // --- Default values ---

    @Test
    fun defaultQualityIs80() {
        val config = buildConfig()
        assertEquals(80, config.quality)
    }

    @Test
    fun defaultFormatIsAuto() {
        val config = buildConfig()
        assertEquals(OutputFormat.AUTO, config.format)
    }

    @Test
    fun defaultResizeModeIsNull() {
        val config = buildConfig()
        assertNull(config.resizeMode)
    }

    // --- Custom values ---

    @Test
    fun customQuality() {
        val config = buildConfig { quality(50) }
        assertEquals(50, config.quality)
    }

    @Test
    fun customFormat() {
        val config = buildConfig { format(OutputFormat.PNG) }
        assertEquals(OutputFormat.PNG, config.format)
    }

    @Test
    fun maxResolutionSetsFitInside() {
        val config = buildConfig { maxResolution(1920, 1080) }
        val mode = config.resizeMode as ResizeMode.FitInside
        assertEquals(1920, mode.maxWidth)
        assertEquals(1080, mode.maxHeight)
    }

    @Test
    fun resizeWithExactResize() {
        val config = buildConfig { resize(ResizeMode.ExactResize(640, 480)) }
        val mode = config.resizeMode as ResizeMode.ExactResize
        assertEquals(640, mode.width)
        assertEquals(480, mode.height)
    }

    @Test
    fun resizeWithPercentage() {
        val config = buildConfig { resize(ResizeMode.Percentage(0.5f)) }
        val mode = config.resizeMode as ResizeMode.Percentage
        assertEquals(0.5f, mode.factor)
    }

    // --- Boundary values ---

    @Test
    fun qualityZeroIsValid() {
        val config = buildConfig { quality(0) }
        assertEquals(0, config.quality)
    }

    @Test
    fun quality100IsValid() {
        val config = buildConfig { quality(100) }
        assertEquals(100, config.quality)
    }

    @Test
    fun percentageAboveOneIsValid() {
        val config = buildConfig { resize(ResizeMode.Percentage(2.0f)) }
        val mode = config.resizeMode as ResizeMode.Percentage
        assertEquals(2.0f, mode.factor)
    }

    // --- Validation errors ---

    @Test
    fun qualityNegativeThrows() {
        assertFailsWith<KomigException.InvalidConfigException> {
            buildConfig { quality(-1) }
        }
    }

    @Test
    fun qualityAbove100Throws() {
        assertFailsWith<KomigException.InvalidConfigException> {
            buildConfig { quality(101) }
        }
    }

    @Test
    fun fitInsideZeroWidthThrows() {
        assertFailsWith<KomigException.InvalidConfigException> {
            buildConfig { maxResolution(0, 100) }
        }
    }

    @Test
    fun fitInsideNegativeHeightThrows() {
        assertFailsWith<KomigException.InvalidConfigException> {
            buildConfig { maxResolution(100, -1) }
        }
    }

    @Test
    fun exactResizeZeroWidthThrows() {
        assertFailsWith<KomigException.InvalidConfigException> {
            buildConfig { resize(ResizeMode.ExactResize(0, 480)) }
        }
    }

    @Test
    fun exactResizeNegativeHeightThrows() {
        assertFailsWith<KomigException.InvalidConfigException> {
            buildConfig { resize(ResizeMode.ExactResize(640, -1)) }
        }
    }

    @Test
    fun percentageZeroThrows() {
        assertFailsWith<KomigException.InvalidConfigException> {
            buildConfig { resize(ResizeMode.Percentage(0f)) }
        }
    }

    @Test
    fun percentageNegativeThrows() {
        assertFailsWith<KomigException.InvalidConfigException> {
            buildConfig { resize(ResizeMode.Percentage(-0.5f)) }
        }
    }
}
