package com.example.ui.editor.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SnippetItem(
    val title: String,
    val category: String,
    val description: String,
    val code: String
)

val DEFAULT_SNIPPETS = listOf(
    SnippetItem(
        title = "Tailwind Glassmorphism Card",
        category = "Tailwind UI",
        description = "Sleek translucent card with backdrop blur and border glow",
        code = """
<div class="max-w-sm bg-white/10 backdrop-blur-md border border-white/20 p-6 rounded-2xl shadow-xl">
  <h3 class="text-xl font-bold text-white mb-2">Glassmorphism Title</h3>
  <p class="text-slate-300 text-sm mb-4">Clean semi-transparent backdrop card style.</p>
  <button class="px-4 py-2 bg-indigo-500 hover:bg-indigo-400 text-white font-semibold rounded-lg transition">Action</button>
</div>
        """.trimIndent()
    ),
    SnippetItem(
        title = "Responsive 3-Column Grid",
        category = "Layout",
        description = "Grid layout scaling from 1 column on mobile to 3 on desktop",
        code = """
<div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 my-6">
  <div class="bg-slate-800 p-5 rounded-xl border border-slate-700">Column 1</div>
  <div class="bg-slate-800 p-5 rounded-xl border border-slate-700">Column 2</div>
  <div class="bg-slate-800 p-5 rounded-xl border border-slate-700">Column 3</div>
</div>
        """.trimIndent()
    ),
    SnippetItem(
        title = "Gradient Hero Banner",
        category = "Hero Sections",
        description = "Eye-catching gradient text title and CTA buttons",
        code = """
<div class="text-center py-12 px-4">
  <h1 class="text-4xl sm:text-6xl font-black tracking-tight text-white mb-4">
    Next Generation <span class="bg-gradient-to-r from-cyan-400 to-indigo-500 bg-clip-text text-transparent">Powerhouse Studio</span>
  </h1>
  <p class="text-slate-400 text-base max-w-xl mx-auto mb-6">Build responsive web UIs right inside your pocket.</p>
  <button class="px-6 py-3 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl shadow-lg">Get Started</button>
</div>
        """.trimIndent()
    ),
    SnippetItem(
        title = "Flexbox Header Navbar",
        category = "Navigation",
        description = "Header bar with brand logo and action button",
        code = """
<header class="flex justify-between items-center py-4 px-6 bg-slate-900 border-b border-slate-800">
  <div class="text-lg font-extrabold text-indigo-400">⚡ APP LOGO</div>
  <div class="flex gap-4">
    <button class="text-sm text-slate-300 hover:text-white">Features</button>
    <button class="px-4 py-1.5 bg-indigo-600 text-white text-sm font-semibold rounded-lg">Sign In</button>
  </div>
</header>
        """.trimIndent()
    ),
    SnippetItem(
        title = "Dark Mode Toggle Script",
        category = "JavaScript",
        description = "Inline JS function to toggle dark class on html element",
        code = """
<script>
  function toggleDarkMode() {
    document.documentElement.classList.toggle('dark');
    console.log('Dark mode toggled!');
  }
</script>
        """.trimIndent()
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnippetSheet(
    onSelectSnippet: (SnippetItem) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Code Snippets Library",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(DEFAULT_SNIPPETS) { snippet ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectSnippet(snippet)
                                onDismiss()
                            }
                            .testTag("snippet_item_${snippet.title}"),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = snippet.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = snippet.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Text(
                                text = snippet.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Text(
                                text = snippet.code.take(120) + "...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
