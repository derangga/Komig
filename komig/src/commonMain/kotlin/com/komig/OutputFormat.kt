package com.komig

/** Supported image output formats. */
enum class OutputFormat {
    JPEG,
    PNG,
    WEBP,

    /** Preserve the input format; falls back to JPEG if detection fails. */
    AUTO,
}
