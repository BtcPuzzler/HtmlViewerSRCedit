package com.example.ui.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.editor.SplitMode

@Composable
fun QuickToolbar(
    onInsertText: (String) -> Unit,
    splitMode: SplitMode,
    onCycleSplitMode: () -> Unit,
    onOpenSnippets: () -> Unit,
    onOpenColorPicker: () -> Unit,
    onFormatCode: () -> Unit,
    onToggleConsole: () -> Unit,
    consoleLogCount: Int,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onToggleFindReplace: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Action Controls Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Split View Mode Selector
                IconButton(
                    onClick = onCycleSplitMode,
                    modifier = Modifier.testTag("split_mode_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Splitscreen,
                        contentDescription = "Split View Mode: ${splitMode.label}",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Snippets Library
                IconButton(
                    onClick = onOpenSnippets,
                    modifier = Modifier.testTag("snippets_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.IntegrationInstructions,
                        contentDescription = "Code Snippets",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Tailwind Color Picker
                IconButton(
                    onClick = onOpenColorPicker,
                    modifier = Modifier.testTag("color_picker_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = "Tailwind Colors",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Format Code Tidy
                IconButton(
                    onClick = onFormatCode,
                    modifier = Modifier.testTag("format_code_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoFixHigh,
                        contentDescription = "Format Code",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Find and Replace
                IconButton(
                    onClick = onToggleFindReplace,
                    modifier = Modifier.testTag("find_replace_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FindInPage,
                        contentDescription = "Find and Replace",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Dev Console Toggle
                IconButton(
                    onClick = onToggleConsole,
                    modifier = Modifier.testTag("dev_console_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (consoleLogCount > 0) {
                                Badge {
                                    Text(consoleLogCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "Dev Console Logs",
                            tint = if (consoleLogCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Undo / Redo
                Row {
                    IconButton(
                        onClick = onUndo,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("undo_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onRedo,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("redo_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Code Helper Bar (Keys horizontally scrollable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val keys = listOf(
                    "<div" to "<div class=\"\">",
                    "</div>" to "</div>",
                    "class=\"\"" to "class=\"\"",
                    "id=\"\"" to "id=\"\"",
                    "<span" to "<span class=\"\"></span>",
                    "<p>" to "<p></p>",
                    "<h1>" to "<h1></h1>",
                    "<button" to "<button class=\"px-4 py-2 bg-indigo-600 text-white rounded-lg\">Button</button>",
                    "<script>" to "<script>\n  \n</script>",
                    "<style>" to "<style>\n  \n</style>",
                    "<!--" to "<!--  -->",
                    "flex" to "flex items-center justify-between",
                    "grid" to "grid grid-cols-2 gap-4",
                    "{" to "{", "}" to "}",
                    "[" to "[", "]" to "]",
                    "\"" to "\"", "'" to "'",
                    "=" to "=", ">" to ">", "/" to "/"
                )

                keys.forEach { (label, valToInsert) ->
                    Surface(
                        onClick = { onInsertText(valToInsert) },
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
