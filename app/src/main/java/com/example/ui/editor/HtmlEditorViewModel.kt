package com.example.ui.editor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.HtmlFile
import com.example.data.repository.HtmlRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HtmlEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HtmlRepository

    val allFiles: StateFlow<List<HtmlFile>>

    private val _currentFile = MutableStateFlow<HtmlFile?>(null)
    val currentFile: StateFlow<HtmlFile?> = _currentFile.asStateFlow()

    private val _codeContent = MutableStateFlow("")
    val codeContent: StateFlow<String> = _codeContent.asStateFlow()

    private val _splitMode = MutableStateFlow(SplitMode.SPLIT_HORIZONTAL)
    val splitMode: StateFlow<SplitMode> = _splitMode.asStateFlow()

    private val _splitRatio = MutableStateFlow(0.5f)
    val splitRatio: StateFlow<Float> = _splitRatio.asStateFlow()

    private val _deviceViewport = MutableStateFlow(DeviceViewport.FULL)
    val deviceViewport: StateFlow<DeviceViewport> = _deviceViewport.asStateFlow()

    private val _codeTheme = MutableStateFlow(CodeTheme.VS_DARK)
    val codeTheme: StateFlow<CodeTheme> = _codeTheme.asStateFlow()

    private val _consoleLogs = MutableStateFlow<List<DevConsoleLog>>(emptyList())
    val consoleLogs: StateFlow<List<DevConsoleLog>> = _consoleLogs.asStateFlow()

    private val _isConsoleOpen = MutableStateFlow(false)
    val isConsoleOpen: StateFlow<Boolean> = _isConsoleOpen.asStateFlow()

    private val _autoInjectTailwind = MutableStateFlow(true)
    val autoInjectTailwind: StateFlow<Boolean> = _autoInjectTailwind.asStateFlow()

    private val _isFindReplaceVisible = MutableStateFlow(false)
    val isFindReplaceVisible: StateFlow<Boolean> = _isFindReplaceVisible.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _replaceQuery = MutableStateFlow("")
    val replaceQuery: StateFlow<String> = _replaceQuery.asStateFlow()

    // Undo / Redo history
    private val undoStack = mutableListOf<String>()
    private val redoStack = mutableListOf<String>()

    private var autoSaveJob: Job? = null

    init {
        val dao = AppDatabase.getDatabase(application).htmlFileDao()
        repository = HtmlRepository(dao)

        val filesFlow = MutableStateFlow<List<HtmlFile>>(emptyList())
        allFiles = filesFlow.asStateFlow()

        viewModelScope.launch {
            repository.checkAndSeedDefaults()
            repository.allFiles.collect { files ->
                filesFlow.value = files
                if (_currentFile.value == null && files.isNotEmpty()) {
                    loadFile(files.first())
                }
            }
        }
    }

    fun loadFile(file: HtmlFile) {
        _currentFile.value = file
        _codeContent.value = file.content
        undoStack.clear()
        redoStack.clear()
        undoStack.add(file.content)
        _consoleLogs.value = emptyList()
    }

    fun createNewProject(title: String, initialTemplate: String = "BLANK") {
        viewModelScope.launch {
            val starterContent = when (initialTemplate) {
                "TAILWIND_CARD" -> """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>$title</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-900 text-white min-h-screen flex items-center justify-center p-6">
  <div class="max-w-sm bg-slate-800 border border-slate-700 rounded-2xl p-6 shadow-xl">
    <div class="w-12 h-12 bg-indigo-500/20 text-indigo-400 rounded-xl flex items-center justify-center font-bold text-xl mb-4">
      ⚡
    </div>
    <h2 class="text-xl font-bold mb-2">$title</h2>
    <p class="text-slate-400 text-sm mb-4">Start crafting your custom HTML component with Tailwind CSS utilities.</p>
    <button class="w-full py-2 bg-indigo-600 hover:bg-indigo-500 font-semibold text-sm rounded-lg transition">
      Action Button
    </button>
  </div>
</body>
</html>
                """.trimIndent()
                else -> """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>$title</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-950 text-slate-100 font-sans p-8">
  <div class="max-w-md mx-auto text-center">
    <h1 class="text-3xl font-extrabold text-indigo-400 mb-2">Hello World</h1>
    <p class="text-slate-400">Edit this HTML code to build your app.</p>
  </div>
</body>
</html>
                """.trimIndent()
            }

            val newFile = HtmlFile(
                title = title.ifBlank { "Untitled Project" },
                content = starterContent
            )
            val id = repository.saveFile(newFile)
            val saved = repository.getFileById(id)
            if (saved != null) {
                loadFile(saved)
            }
        }
    }

    fun updateCodeContent(newContent: String) {
        if (newContent == _codeContent.value) return

        if (undoStack.isEmpty() || undoStack.last() != newContent) {
            undoStack.add(newContent)
            if (undoStack.size > 50) undoStack.removeAt(0)
            redoStack.clear()
        }

        _codeContent.value = newContent

        // Auto save with debounce
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(1000)
            _currentFile.value?.let { file ->
                repository.saveFile(file.copy(content = newContent))
            }
        }
    }

    fun undo() {
        if (undoStack.size > 1) {
            val current = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(current)
            val previous = undoStack.last()
            _codeContent.value = previous
            triggerSave(previous)
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(next)
            _codeContent.value = next
            triggerSave(next)
        }
    }

    private fun triggerSave(content: String) {
        viewModelScope.launch {
            _currentFile.value?.let { file ->
                repository.saveFile(file.copy(content = content))
            }
        }
    }

    fun insertTextAtCursor(textToInsert: String, currentText: String, cursorPosition: Int): Pair<String, Int> {
        val safePos = cursorPosition.coerceIn(0, currentText.length)
        val newText = currentText.substring(0, safePos) + textToInsert + currentText.substring(safePos)
        val newCursor = safePos + textToInsert.length
        updateCodeContent(newText)
        return Pair(newText, newCursor)
    }

    fun insertSnippet(snippetContent: String) {
        updateCodeContent(_codeContent.value + "\n" + snippetContent)
    }

    fun formatCode() {
        val unformatted = _codeContent.value
        // Basic HTML tidier/formatter
        val formatted = tidyHtml(unformatted)
        updateCodeContent(formatted)
    }

    private fun tidyHtml(html: String): String {
        val lines = html.lines()
        val builder = StringBuilder()
        var indent = 0
        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty()) {
                builder.append("\n")
                continue
            }
            if (line.startsWith("</") || line.startsWith("</div>") || line.startsWith("</section>")) {
                indent = (indent - 1).coerceAtLeast(0)
            }
            builder.append("  ".repeat(indent)).append(line).append("\n")
            if (line.startsWith("<") && !line.startsWith("</") && !line.endsWith("/>") && !line.startsWith("<!") && !line.contains("</")) {
                if (!line.startsWith("<img") && !line.startsWith("<input") && !line.startsWith("<br") && !line.startsWith("<hr") && !line.startsWith("<meta") && !line.startsWith("<link")) {
                    indent++
                }
            }
        }
        return builder.toString().trimEnd()
    }

    fun setSplitMode(mode: SplitMode) {
        _splitMode.value = mode
    }

    fun setSplitRatio(ratio: Float) {
        _splitRatio.value = ratio.coerceIn(0.15f, 0.85f)
    }

    fun setDeviceViewport(viewport: DeviceViewport) {
        _deviceViewport.value = viewport
    }

    fun setCodeTheme(theme: CodeTheme) {
        _codeTheme.value = theme
    }

    fun toggleAutoInjectTailwind() {
        _autoInjectTailwind.value = !_autoInjectTailwind.value
    }

    fun toggleConsole() {
        _isConsoleOpen.value = !_isConsoleOpen.value
    }

    fun addConsoleLog(log: DevConsoleLog) {
        _consoleLogs.value = _consoleLogs.value + log
    }

    fun clearConsoleLogs() {
        _consoleLogs.value = emptyList()
    }

    fun toggleFindReplace() {
        _isFindReplaceVisible.value = !_isFindReplaceVisible.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setReplaceQuery(query: String) {
        _replaceQuery.value = query
    }

    fun replaceAll() {
        val search = _searchQuery.value
        val replace = _replaceQuery.value
        if (search.isNotEmpty()) {
            val updated = _codeContent.value.replace(search, replace)
            updateCodeContent(updated)
        }
    }

    fun deleteCurrentProject() {
        viewModelScope.launch {
            _currentFile.value?.let { file ->
                repository.deleteFile(file.id)
                val remaining = repository.allFiles.firstOrNull() ?: emptyList()
                if (remaining.isNotEmpty()) {
                    loadFile(remaining.first())
                } else {
                    createNewProject("My New Project")
                }
            }
        }
    }

    fun renameProject(newTitle: String) {
        viewModelScope.launch {
            _currentFile.value?.let { file ->
                val updated = file.copy(title = newTitle)
                repository.saveFile(updated)
                _currentFile.value = updated
            }
        }
    }
}
