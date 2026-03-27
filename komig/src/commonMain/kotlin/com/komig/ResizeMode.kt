package com.komig

/** Describes how an image should be resized during compression. */
sealed class ResizeMode {

    /**
     * Scale the image so that it fits inside [maxWidth] x [maxHeight],
     * preserving the aspect ratio. No upscaling is applied.
     */
    data class FitInside(val maxWidth: Int, val maxHeight: Int) : ResizeMode()

    /** Resize the image to exactly [width] x [height], ignoring the aspect ratio. */
    data class ExactResize(val width: Int, val height: Int) : ResizeMode()

    /** Scale both dimensions by [factor] (e.g. 0.5 = half size). */
    data class Percentage(val factor: Float) : ResizeMode()
}
