package com.komig.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komig.CompressionResult
import com.komig.Komig
import com.komig.OutputFormat
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CompressUiState(
    val originalBytes: ByteArray? = null,
    val result: CompressionResult? = null,
    val isCompressing: Boolean = false,
    val error: String? = null,
    val quality: Int = 80,
    val format: OutputFormat = OutputFormat.AUTO,
    val maxWidth: Int = 0,
    val maxHeight: Int = 0,
)

class CompressViewModel : ViewModel() {

    private val _state = MutableStateFlow(CompressUiState())
    val state: StateFlow<CompressUiState> = _state.asStateFlow()

    private var compressJob: Job? = null

    fun onImagePicked(bytes: ByteArray) {
        _state.update { it.copy(originalBytes = bytes, result = null, error = null) }
    }

    fun onQualityChanged(quality: Int) {
        _state.update { it.copy(quality = quality) }
    }

    fun onFormatChanged(format: OutputFormat) {
        _state.update { it.copy(format = format) }
    }

    fun onMaxResolutionChanged(width: Int, height: Int) {
        _state.update { it.copy(maxWidth = width, maxHeight = height) }
    }

    fun compress() {
        val current = _state.value
        val input = current.originalBytes ?: return

        compressJob?.cancel()
        compressJob = viewModelScope.launch {
            _state.update { it.copy(isCompressing = true, error = null) }
            try {
                val result = Komig.compress(input) {
                    quality(current.quality)
                    format(current.format)
                    if (current.maxWidth > 0 && current.maxHeight > 0) {
                        maxResolution(current.maxWidth, current.maxHeight)
                    }
                }
                _state.update { it.copy(result = result, isCompressing = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Compression failed",
                        isCompressing = false,
                    )
                }
            }
        }
    }
}
