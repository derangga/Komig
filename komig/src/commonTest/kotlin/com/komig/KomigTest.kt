package com.komig

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class KomigTest {

    @Test
    fun invalidQualityThrowsInvalidConfigException() = runTest {
        assertFailsWith<KomigException.InvalidConfigException> {
            Komig.compress(TestFixtures.MINIMAL_JPEG) {
                quality(150)
            }
        }
    }

    @Test
    fun autoFormatWithUnknownBytesThrowsUnsupportedFormat() = runTest {
        assertFailsWith<KomigException.UnsupportedFormatException> {
            Komig.compress(TestFixtures.UNKNOWN_FORMAT) {
                format(OutputFormat.AUTO)
            }
        }
    }

    @Test
    fun autoFormatWithEmptyBytesThrowsUnsupportedFormat() = runTest {
        assertFailsWith<KomigException.UnsupportedFormatException> {
            Komig.compress(TestFixtures.EMPTY)
        }
    }

}
