package com.google.ai.edge.gallery.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DebugMetricsOverlay(
  timeToFirstToken: Float?,
  decodeSpeed: Float?,
  modifier: Modifier = Modifier
) {
  if (timeToFirstToken != null || decodeSpeed != null) {
    val ttftStr = timeToFirstToken?.let { String.format("%.2fs", it) } ?: "N/A"
    val tpsStr = decodeSpeed?.let { String.format("%.1f tok/s", it) } ?: "N/A"

    Box(
      modifier = modifier
        .background(Color.Black.copy(alpha = 0.6f))
        .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Text(
        text = "TTFT: $ttftStr | TPS: $tpsStr",
        color = Color.White,
        style = MaterialTheme.typography.labelSmall
      )
    }
  }
}
