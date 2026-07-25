package com.example.ui.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

data class TailwindColorSwatch(
    val name: String,
    val bgClass: String,
    val textClass: String,
    val hex: Color
)

val TAILWIND_SWATCHES = listOf(
    TailwindColorSwatch("Indigo 600", "bg-indigo-600", "text-indigo-600", Color(0xFF4F46E5)),
    TailwindColorSwatch("Cyan 500", "bg-cyan-500", "text-cyan-500", Color(0xFF06B6D4)),
    TailwindColorSwatch("Blue 500", "bg-blue-500", "text-blue-500", Color(0xFF3B82F6)),
    TailwindColorSwatch("Emerald 500", "bg-emerald-500", "text-emerald-500", Color(0xFF10B981)),
    TailwindColorSwatch("Rose 500", "bg-rose-500", "text-rose-500", Color(0xFFF43F5E)),
    TailwindColorSwatch("Amber 500", "bg-amber-500", "text-amber-500", Color(0xFFF59E0B)),
    TailwindColorSwatch("Purple 600", "bg-purple-600", "text-purple-600", Color(0xFF9333EA)),
    TailwindColorSwatch("Slate 900", "bg-slate-900", "text-slate-900", Color(0xFF0F172A)),
    TailwindColorSwatch("Slate 800", "bg-slate-800", "text-slate-800", Color(0xFF1E293B)),
    TailwindColorSwatch("Slate 100", "bg-slate-100", "text-slate-100", Color(0xFFF1F5F9)),
    TailwindColorSwatch("Zinc 700", "bg-zinc-700", "text-zinc-700", Color(0xFF3F3F46)),
    TailwindColorSwatch("Teal 400", "bg-teal-400", "text-teal-400", Color(0xFF2DD4BF))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerSheet(
    onSelectColorClass: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Tailwind Color Palette",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(TAILWIND_SWATCHES) { swatch ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectColorClass(swatch.bgClass)
                                onDismiss()
                            }
                            .testTag("color_swatch_${swatch.name}"),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(swatch.hex)
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            )
                            Column {
                                Text(
                                    text = swatch.name,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = swatch.bgClass,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
