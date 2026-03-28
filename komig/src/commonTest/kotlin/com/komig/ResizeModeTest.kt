package com.komig

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals

class ResizeModeTest {

    // --- FitInside ---

    @Test
    fun fitInsideProperties() {
        val mode = ResizeMode.FitInside(1920, 1080)
        assertEquals(1920, mode.maxWidth)
        assertEquals(1080, mode.maxHeight)
    }

    @Test
    fun fitInsideEquality() {
        val a = ResizeMode.FitInside(1920, 1080)
        val b = ResizeMode.FitInside(1920, 1080)
        assertEquals(a, b)
    }

    @Test
    fun fitInsideInequality() {
        val a = ResizeMode.FitInside(1920, 1080)
        val b = ResizeMode.FitInside(1280, 720)
        assertNotEquals(a, b)
    }

    @Test
    fun fitInsideCopy() {
        val original = ResizeMode.FitInside(1920, 1080)
        val copied = original.copy(maxWidth = 800)
        assertEquals(800, copied.maxWidth)
        assertEquals(1080, copied.maxHeight)
    }

    // --- ExactResize ---

    @Test
    fun exactResizeProperties() {
        val mode = ResizeMode.ExactResize(640, 480)
        assertEquals(640, mode.width)
        assertEquals(480, mode.height)
    }

    @Test
    fun exactResizeEquality() {
        assertEquals(
            ResizeMode.ExactResize(640, 480),
            ResizeMode.ExactResize(640, 480),
        )
    }

    @Test
    fun exactResizeCopy() {
        val original = ResizeMode.ExactResize(640, 480)
        val copied = original.copy(height = 360)
        assertEquals(640, copied.width)
        assertEquals(360, copied.height)
    }

    // --- Percentage ---

    @Test
    fun percentageProperties() {
        val mode = ResizeMode.Percentage(0.5f)
        assertEquals(0.5f, mode.factor)
    }

    @Test
    fun percentageEquality() {
        assertEquals(
            ResizeMode.Percentage(0.75f),
            ResizeMode.Percentage(0.75f),
        )
    }

    @Test
    fun percentageCopy() {
        val original = ResizeMode.Percentage(0.5f)
        val copied = original.copy(factor = 0.25f)
        assertEquals(0.25f, copied.factor)
    }

    // --- Type checking ---

    @Test
    fun fitInsideIsResizeMode() {
        val mode: ResizeMode = ResizeMode.FitInside(100, 100)
        assertIs<ResizeMode.FitInside>(mode)
    }

    @Test
    fun exactResizeIsResizeMode() {
        val mode: ResizeMode = ResizeMode.ExactResize(100, 100)
        assertIs<ResizeMode.ExactResize>(mode)
    }

    @Test
    fun percentageIsResizeMode() {
        val mode: ResizeMode = ResizeMode.Percentage(0.5f)
        assertIs<ResizeMode.Percentage>(mode)
    }
}
