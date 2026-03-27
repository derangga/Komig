package com.komig

/**
 * Immutable configuration for an image compression operation.
 * Construct via [Builder] DSL.
 */
class CompressionConfig private constructor(
    val quality: Int,
    val format: OutputFormat,
    val resizeMode: ResizeMode?,
) {

    /** DSL builder for [CompressionConfig]. */
    class Builder {
        private var quality: Int = DEFAULT_QUALITY
        private var format: OutputFormat = OutputFormat.AUTO
        private var resizeMode: ResizeMode? = null

        /** Set the compression quality (0–100). */
        fun quality(value: Int) {
            quality = value
        }

        /** Set the desired output format. */
        fun format(value: OutputFormat) {
            format = value
        }

        /**
         * Convenience shorthand for [ResizeMode.FitInside].
         * The image is scaled to fit inside [maxWidth] x [maxHeight].
         */
        fun maxResolution(maxWidth: Int, maxHeight: Int) {
            resizeMode = ResizeMode.FitInside(maxWidth, maxHeight)
        }

        /** Set an explicit [ResizeMode]. */
        fun resize(mode: ResizeMode) {
            resizeMode = mode
        }

        internal fun build(): CompressionConfig {
            validate()
            return CompressionConfig(
                quality = quality,
                format = format,
                resizeMode = resizeMode,
            )
        }

        private fun validate() {
            if (quality !in 0..100) {
                throw KomigException.InvalidConfigException(
                    "Quality must be in 0..100, was $quality",
                )
            }
            when (val mode = resizeMode) {
                is ResizeMode.FitInside -> {
                    if (mode.maxWidth <= 0 || mode.maxHeight <= 0) {
                        throw KomigException.InvalidConfigException(
                            "FitInside dimensions must be > 0",
                        )
                    }
                }
                is ResizeMode.ExactResize -> {
                    if (mode.width <= 0 || mode.height <= 0) {
                        throw KomigException.InvalidConfigException(
                            "ExactResize dimensions must be > 0",
                        )
                    }
                }
                is ResizeMode.Percentage -> {
                    if (mode.factor <= 0f) {
                        throw KomigException.InvalidConfigException(
                            "Percentage factor must be > 0",
                        )
                    }
                }
                null -> Unit
            }
        }
    }

    companion object {
        const val DEFAULT_QUALITY = 80
    }
}
