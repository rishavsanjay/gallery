/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ai.edge.gallery.ui.common.chat

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import com.google.ai.edge.gallery.ui.common.humanReadableDuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.google.ai.edge.gallery.ui.common.DebugMetricsOverlay

/** Composable function to display the latency of a chat message, if available. */
@Composable
fun LatencyText(message: ChatMessage) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    if (message.latencyMs >= 0) {
      Text(
        message.latencyMs.humanReadableDuration(),
        modifier = Modifier.alpha(0.5f).testTag("latency_label"),
        style = MaterialTheme.typography.labelSmall,
      )
    }

    if (message is ChatMessageText && (message.timeToFirstToken != null || message.decodeSpeed != null)) {
      Spacer(modifier = Modifier.width(8.dp))
      DebugMetricsOverlay(
        timeToFirstToken = message.timeToFirstToken,
        decodeSpeed = message.decodeSpeed
      )
    }
  }
}
