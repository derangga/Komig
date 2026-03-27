package com.komig.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.komig.OutputFormat

@Composable
fun CompressScreen(
    state: CompressUiState,
    onPickImage: () -> Unit,
    onUseDemoImage: () -> Unit,
    onCompress: () -> Unit,
    onSaveImage: () -> Unit,
    onQualityChanged: (Int) -> Unit,
    onFormatChanged: (OutputFormat) -> Unit,
    onMaxResolutionChanged: (Int, Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Komig Compressor",
            style = MaterialTheme.typography.headlineMedium,
        )

        // Action buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ElevatedButton(onClick = onPickImage) {
                Text("Pick Image")
            }
            FilledTonalButton(onClick = onUseDemoImage) {
                Text("Use Demo Image")
            }
        }

        // Loading indicator
        if (state.isCompressing) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }

        // Error
        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        // Preview: Before / After
        if (state.originalBytes != null) {
            PreviewSection(
                label = "Original",
                bytes = state.originalBytes,
            )
        }

        state.result?.let { result ->
            PreviewSection(
                label = "Compressed",
                bytes = result.bytes,
            )

            // Stats card
            StatsCard(state)

            // Save button
            FilledTonalButton(
                onClick = onSaveImage,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Save Image")
            }
        }

        // Controls (only show after image is picked)
        if (state.originalBytes != null) {
            ControlsSection(
                quality = state.quality,
                format = state.format,
                maxWidth = state.maxWidth,
                maxHeight = state.maxHeight,
                onQualityChanged = onQualityChanged,
                onFormatChanged = onFormatChanged,
                onMaxResolutionChanged = onMaxResolutionChanged,
            )

            Button(
                onClick = onCompress,
                enabled = !state.isCompressing,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.isCompressing) "Compressing..." else "Compress")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun PreviewSection(label: String, bytes: ByteArray) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(modifier = Modifier.height(4.dp))
        val bitmap: ImageBitmap = remember(bytes) { bytes.toImageBitmap() }
        Image(
            bitmap = bitmap,
            contentDescription = label,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun StatsCard(state: CompressUiState) {
    val result = state.result ?: return
    val originalSize = result.inputSizeBytes
    val compressedSize = result.outputSizeBytes
    val savings = if (originalSize > 0) {
        ((originalSize - compressedSize) * 100f / originalSize)
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Stats", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Original: ${formatBytes(originalSize)}")
            Text("Compressed: ${formatBytes(compressedSize)}")
            Text("Savings: ${roundToOneDecimal(savings)}%")
            Text("Dimensions: ${result.width} x ${result.height}")
            Text("Format: ${result.format.name}")
        }
    }
}

private fun roundToOneDecimal(value: Float): String {
    val rounded = kotlin.math.round(value * 10) / 10.0
    return rounded.toString()
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1_048_576 -> {
            val mb = kotlin.math.round(bytes / 1_048_576f * 100) / 100.0
            "$mb MB"
        }
        bytes >= 1024 -> {
            val kb = kotlin.math.round(bytes / 1024f * 10) / 10.0
            "$kb KB"
        }
        else -> "$bytes B"
    }
}

@Composable
private fun ControlsSection(
    quality: Int,
    format: OutputFormat,
    maxWidth: Int,
    maxHeight: Int,
    onQualityChanged: (Int) -> Unit,
    onFormatChanged: (OutputFormat) -> Unit,
    onMaxResolutionChanged: (Int, Int) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Controls", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Quality slider
            Text("Quality: $quality")
            Slider(
                value = quality.toFloat(),
                onValueChange = { onQualityChanged(it.toInt()) },
                valueRange = 0f..100f,
                steps = 0,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Format selector
            Text("Format")
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                OutputFormat.entries.forEach { fmt ->
                    FilterChip(
                        selected = format == fmt,
                        onClick = { onFormatChanged(fmt) },
                        label = { Text(fmt.name) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Max resolution
            Text("Max Resolution (0 = no limit)")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                var widthText by remember(maxWidth) { mutableStateOf(if (maxWidth > 0) maxWidth.toString() else "") }
                var heightText by remember(maxHeight) { mutableStateOf(if (maxHeight > 0) maxHeight.toString() else "") }

                OutlinedTextField(
                    value = widthText,
                    onValueChange = { text ->
                        widthText = text
                        val w = text.toIntOrNull() ?: 0
                        onMaxResolutionChanged(w, heightText.toIntOrNull() ?: 0)
                    },
                    label = { Text("Width") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = heightText,
                    onValueChange = { text ->
                        heightText = text
                        val h = text.toIntOrNull() ?: 0
                        onMaxResolutionChanged(widthText.toIntOrNull() ?: 0, h)
                    },
                    label = { Text("Height") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
            }
        }
    }
}
