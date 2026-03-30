package com.komig

/** Sealed exception hierarchy for all Komig errors. */
sealed class KomigException(message: String, cause: Throwable? = null) :
    Exception(message, cause) {

    /** The input image format is not recognized or not supported. */
    class UnsupportedFormatException(message: String, cause: Throwable? = null) :
        KomigException(message, cause)

    /** The platform codec failed to decode the input bytes. */
    class DecodingException(message: String, cause: Throwable? = null) :
        KomigException(message, cause)

    /** The platform codec failed to encode the output bytes. */
    class EncodingException(message: String, cause: Throwable? = null) :
        KomigException(message, cause)

    /** The compression configuration is invalid. */
    class InvalidConfigException(message: String, cause: Throwable? = null) :
        KomigException(message, cause)

    /** The file at the given path could not be read. */
    class FileIOException(message: String, cause: Throwable? = null) :
        KomigException(message, cause)
}
