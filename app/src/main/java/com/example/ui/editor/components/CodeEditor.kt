package com.example.ui.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.editor.CodeTheme

@Composable
fun CodeEditor(
    codeContent: String,
    onCodeChange: (String) -> Unit,
    theme: CodeTheme,
    modifier: Modifier = Modifier,
    onCursorPositionChanged: (Int) -> Unit = {}
) {
    var tfValue by remember(codeContent) {
        mutableStateOf(TextFieldValue(codeContent))
    }

    val lines = codeContent.lines()
    val lineCount = lines.size.coerceAtLeast(1)
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(theme.bg)
    ) {
        // Line Numbers Gutter
        Box(
            modifier = Modifier
                .width(44.dp)
                .fillMaxHeight()
                .background(theme.bg.copy(alpha = 0.95f))
                .verticalScroll(scrollState)
                .padding(vertical = 12.dp, horizontal = 4.dp)
        ) {
            val lineNumbersText = (1..lineCount).joinToString("\n")
            Text(
                text = lineNumbersText,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = theme.lineNumColor,
                    textAlign = TextAlign.End
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Code Editor Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(scrollState)
                .padding(vertical = 12.dp, horizontal = 8.dp)
        ) {
            BasicTextField(
                value = tfValue,
                onValueChange = { newValue ->
                    tfValue = newValue
                    onCodeChange(newValue.text)
                    onCursorPositionChanged(newValue.selection.start)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("code_editor_input"),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = theme.textColor
                ),
                cursorBrush = SolidColor(theme.tagColor),
                visualTransformation = HtmlSyntaxTransformation(theme)
            )
        }
    }
}

class HtmlSyntaxTransformation(private val theme: CodeTheme) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val highlighted = buildAnnotatedString {
            val str = text.text
            append(str)

            // Highlight HTML comments <!-- ... -->
            val commentRegex = Regex("<!--[\\s\\S]*?-->")
            for (match in commentRegex.findAll(str)) {
                addStyle(
                    SpanStyle(color = theme.lineNumColor),
                    match.range.first,
                    match.range.last + 1
                )
            }

            // Highlight HTML tags <tag ... > and </tag>
            val tagRegex = Regex("</?[a-zA-Z0-9\\-]+")
            for (match in tagRegex.findAll(str)) {
                addStyle(
                    SpanStyle(color = theme.tagColor),
                    match.range.first,
                    match.range.last + 1
                )
            }

            // Highlight tag brackets < >
            val bracketRegex = Regex("[<>]")
            for (match in bracketRegex.findAll(str)) {
                addStyle(
                    SpanStyle(color = theme.tagColor),
                    match.range.first,
                    match.range.last + 1
                )
            }

            // Highlight attributes like class=, id=, src=, href=
            val attrRegex = Regex("\\b[a-zA-Z0-9\\-]+(?=\\=)")
            for (match in attrRegex.findAll(str)) {
                addStyle(
                    SpanStyle(color = theme.attrColor),
                    match.range.first,
                    match.range.last + 1
                )
            }

            // Highlight string values "..." or '...'
            val stringRegex = Regex("\"[^\"]*\"|'[^']*'")
            for (match in stringRegex.findAll(str)) {
                addStyle(
                    SpanStyle(color = theme.stringColor),
                    match.range.first,
                    match.range.last + 1
                )
            }
        }
        return TransformedText(highlighted, OffsetMapping.Identity)
    }
}
