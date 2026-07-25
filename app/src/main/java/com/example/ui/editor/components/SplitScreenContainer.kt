package com.example.ui.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.ui.editor.SplitMode

@Composable
fun SplitScreenContainer(
    splitMode: SplitMode,
    splitRatio: Float,
    onSplitRatioChange: (Float) -> Unit,
    editorContent: @Composable (Modifier) -> Unit,
    previewContent: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (splitMode) {
            SplitMode.EDITOR_ONLY -> {
                editorContent(Modifier.fillMaxSize())
            }

            SplitMode.PREVIEW_ONLY -> {
                previewContent(Modifier.fillMaxSize())
            }

            SplitMode.SPLIT_HORIZONTAL -> { // Staked vertically
                Column(modifier = Modifier.fillMaxSize()) {
                    editorContent(
                        Modifier
                            .fillMaxWidth()
                            .weight(splitRatio)
                    )

                    // Horizontal divider drag handle
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val totalHeight = size.height
                                    val newRatio = splitRatio + (dragAmount.y / 800f)
                                    onSplitRatioChange(newRatio)
                                }
                            }
                    )

                    previewContent(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f - splitRatio)
                    )
                }
            }

            SplitMode.SPLIT_VERTICAL -> { // Side by side
                Row(modifier = Modifier.fillMaxSize()) {
                    editorContent(
                        Modifier
                            .fillMaxHeight()
                            .weight(splitRatio)
                    )

                    // Vertical divider drag handle
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(6.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val newRatio = splitRatio + (dragAmount.x / 600f)
                                    onSplitRatioChange(newRatio)
                                }
                            }
                    )

                    previewContent(
                        Modifier
                            .fillMaxHeight()
                            .weight(1f - splitRatio)
                    )
                }
            }
        }
    }
}
