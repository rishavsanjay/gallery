package com.google.ai.edge.gallery.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.ai.edge.gallery.ui.common.math.MessageChunk
import com.google.ai.edge.gallery.ui.common.math.tokenizeMarkdownAndMath
import com.hrm.latex.renderer.Latex

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MixedMarkdownMathText(
  text: String,
  modifier: Modifier = Modifier,
  textColor: Color = MaterialTheme.colorScheme.onSurface,
  linkColor: Color = MaterialTheme.colorScheme.primary,
) {
  val chunks = tokenizeMarkdownAndMath(text)

  Column(modifier = modifier) {
    var inlineAccumulator = mutableListOf<MessageChunk>()

    for (chunk in chunks) {
      if (chunk is MessageChunk.Math && !chunk.isInline) {
        // Render accumulated inline content first.
        if (inlineAccumulator.isNotEmpty()) {
          FlowRow {
            for (inlineChunk in inlineAccumulator) {
              when (inlineChunk) {
                is MessageChunk.Text -> MarkdownText(
                  text = inlineChunk.markdown,
                  textColor = textColor,
                  linkColor = linkColor,
                  modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                )
                is MessageChunk.Math -> Latex(
                  latex = inlineChunk.latex,
                  modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                )
              }
            }
          }
          inlineAccumulator.clear()
        }

        // Render block math.
        Latex(latex = chunk.latex, modifier = Modifier.padding(vertical = 8.dp))
      } else {
        inlineAccumulator.add(chunk)
      }
    }

    // Render remaining inline content.
    if (inlineAccumulator.isNotEmpty()) {
      FlowRow {
        for (inlineChunk in inlineAccumulator) {
          when (inlineChunk) {
            is MessageChunk.Text -> MarkdownText(
              text = inlineChunk.markdown,
              textColor = textColor,
              linkColor = linkColor,
              modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
            )
            is MessageChunk.Math -> Latex(
              latex = inlineChunk.latex,
              modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
            )
          }
        }
      }
    }
  }
}
