package com.komig

/** Supported image output formats. */
enum class OutputFormat(val extension: String) {
    JPEG("jpg"),
    PNG("png"),
    WEBP("webp"),

    /** Preserve the input format; falls back to JPEG if detection fails. */
    AUTO("jpg"),
}
