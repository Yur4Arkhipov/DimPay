package com.example.dimpay.core.designsystem.extensions

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

fun String.formatAsExpireDate(): String {
    return when {
        length <= 2 -> this
        else -> "${take(2)}/${drop(2)}"
    }
}

fun String.formatAsCardNumber(): String {
    return chunked(4).joinToString(" ")
}

class ExpireDateTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(4)
        val formatted = buildString {
            trimmed.forEachIndexed { index, char ->
                append(char)
                if (index == 1 && trimmed.length > 2) {
                    append("/")
                }
            }
        }
        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    else -> offset + 1
                }
            }
            override fun transformedToOriginal(offset: Int): Int {

                return when {
                    offset <= 2 -> offset
                    else -> offset - 1
                }
            }
        }
        return TransformedText(
            AnnotatedString(formatted),
            offsetTranslator
        )
    }
}

class CardNumberTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(16)
        val formatted = buildString {
            trimmed.forEachIndexed { index, char ->
                append(char)
                val isLastChar = index == trimmed.lastIndex
                if ((index + 1) % 4 == 0 && !isLastChar) {
                    append(" ")
                }
            }
        }
        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 4 -> offset
                    offset <= 8 -> offset + 1
                    offset <= 12 -> offset + 2
                    offset <= 16 -> offset + 3
                    else -> offset
                }
            }
            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 4 -> offset
                    offset <= 9 -> offset - 1
                    offset <= 14 -> offset - 2
                    offset <= 19 -> offset - 3
                    else -> offset
                }
            }
        }
        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = offsetTranslator
        )
    }
}