package com.google.ai.edge.gallery.ui.common.math

sealed class MessageChunk {
  data class Text(val markdown: String) : MessageChunk()
  data class Math(val latex: String, val isInline: Boolean) : MessageChunk()
}

/**
 * Tokenizes a raw string into a list of Markdown text chunks and Math chunks, based on standard
 * LaTeX math delimiters (`$$` or `\[` ... `\]` for block, `$` or `\(` ... `\)` for inline).
 *
 * It correctly ignores any math delimiters that appear inside Markdown code blocks (`` ` `` or `` ``` ``).
 * It is also resilient to incomplete delimiters during streaming (e.g., treats an unclosed `$$`
 * as a block math chunk extending to the end of the string).
 */
fun tokenizeMarkdownAndMath(input: String): List<MessageChunk> {
  val chunks = mutableListOf<MessageChunk>()
  var i = 0
  var currentText = StringBuilder()

  while (i < input.length) {
    // 1. Check for code blocks
    // Triple backticks
    if (input.startsWith("```", i)) {
      val endIdx = input.indexOf("```", i + 3)
      if (endIdx != -1) {
        currentText.append(input.substring(i, endIdx + 3))
        i = endIdx + 3
      } else {
        // Unclosed triple backtick
        currentText.append(input.substring(i))
        i = input.length
      }
      continue
    }
    // Single backticks
    if (input.startsWith("`", i)) {
      val endIdx = input.indexOf("`", i + 1)
      if (endIdx != -1) {
        currentText.append(input.substring(i, endIdx + 1))
        i = endIdx + 1
      } else {
        // Unclosed single backtick
        currentText.append(input.substring(i))
        i = input.length
      }
      continue
    }

    // 2. Check for display math `$$`
    if (input.startsWith("$$", i)) {
      if (currentText.isNotEmpty()) {
        chunks.add(MessageChunk.Text(currentText.toString()))
        currentText.clear()
      }
      val endIdx = input.indexOf("$$", i + 2)
      if (endIdx != -1) {
        chunks.add(MessageChunk.Math(input.substring(i + 2, endIdx), isInline = false))
        i = endIdx + 2
      } else {
        // Unclosed $$
        chunks.add(MessageChunk.Math(input.substring(i + 2), isInline = false))
        i = input.length
      }
      continue
    }

    // 3. Check for display math `\[` ... `\]`
    if (input.startsWith("\\[", i)) {
      if (currentText.isNotEmpty()) {
        chunks.add(MessageChunk.Text(currentText.toString()))
        currentText.clear()
      }
      val endIdx = input.indexOf("\\]", i + 2)
      if (endIdx != -1) {
        chunks.add(MessageChunk.Math(input.substring(i + 2, endIdx), isInline = false))
        i = endIdx + 2
      } else {
        // Unclosed \[
        chunks.add(MessageChunk.Math(input.substring(i + 2), isInline = false))
        i = input.length
      }
      continue
    }

    // 4. Check for inline math `\(` ... `\)`
    if (input.startsWith("\\(", i)) {
      if (currentText.isNotEmpty()) {
        chunks.add(MessageChunk.Text(currentText.toString()))
        currentText.clear()
      }
      val endIdx = input.indexOf("\\)", i + 2)
      if (endIdx != -1) {
        chunks.add(MessageChunk.Math(input.substring(i + 2, endIdx), isInline = true))
        i = endIdx + 2
      } else {
        // Unclosed \(
        chunks.add(MessageChunk.Math(input.substring(i + 2), isInline = true))
        i = input.length
      }
      continue
    }

    // 5. Check for inline math `$`
    if (input.startsWith("$", i)) {
      if (currentText.isNotEmpty()) {
        chunks.add(MessageChunk.Text(currentText.toString()))
        currentText.clear()
      }
      val endIdx = input.indexOf("$", i + 1)
      if (endIdx != -1) {
        chunks.add(MessageChunk.Math(input.substring(i + 1, endIdx), isInline = true))
        i = endIdx + 1
      } else {
        // Unclosed $
        chunks.add(MessageChunk.Math(input.substring(i + 1), isInline = true))
        i = input.length
      }
      continue
    }

    // Otherwise, append character to text
    currentText.append(input[i])
    i++
  }

  if (currentText.isNotEmpty()) {
    chunks.add(MessageChunk.Text(currentText.toString()))
  }

  return chunks
}
