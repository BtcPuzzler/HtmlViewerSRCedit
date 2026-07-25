package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Phonelink
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.editor.DeviceViewport
import com.example.ui.editor.HtmlEditorViewModel
import com.example.ui.editor.SplitMode
import com.example.ui.editor.components.CodeEditor
import com.example.ui.editor.components.ColorPickerSheet
import com.example.ui.editor.components.DevConsoleView
import com.example.ui.editor.components.FindReplaceBar
import com.example.ui.editor.components.QuickToolbar
import com.example.ui.editor.components.SnippetSheet
import com.example.ui.editor.components.SplitScreenContainer
import com.example.ui.editor.components.WebViewPreview
import com.example.ui.projects.ProjectDrawerContent
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: HtmlEditorViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val allFiles by viewModel.allFiles.collectAsStateWithLifecycle()
                val activeFile by viewModel.currentFile.collectAsStateWithLifecycle()
                val codeContent by viewModel.codeContent.collectAsStateWithLifecycle()
                val splitMode by viewModel.splitMode.collectAsStateWithLifecycle()
                val splitRatio by viewModel.splitRatio.collectAsStateWithLifecycle()
                val deviceViewport by viewModel.deviceViewport.collectAsStateWithLifecycle()
                val codeTheme by viewModel.codeTheme.collectAsStateWithLifecycle()
                val consoleLogs by viewModel.consoleLogs.collectAsStateWithLifecycle()
                val isConsoleOpen by viewModel.isConsoleOpen.collectAsStateWithLifecycle()
                val autoInjectTailwind by viewModel.autoInjectTailwind.collectAsStateWithLifecycle()
                val isFindReplaceVisible by viewModel.isFindReplaceVisible.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val replaceQuery by viewModel.replaceQuery.collectAsStateWithLifecycle()

                var showSnippetSheet by remember { mutableStateOf(false) }
                var showColorPickerSheet by remember { mutableStateOf(false) }
                var showViewportMenu by remember { mutableStateOf(false) }
                var currentCursorPos by remember { mutableIntStateOf(0) }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ProjectDrawerContent(
                            projects = allFiles,
                            activeProject = activeFile,
                            onSelectProject = { file -> viewModel.loadFile(file) },
                            onCreateProject = { title, template ->
                                viewModel.createNewProject(title, template)
                            },
                            onDeleteProject = { viewModel.deleteCurrentProject() },
                            onRenameProject = { newTitle -> viewModel.renameProject(newTitle) },
                            currentTheme = codeTheme,
                            onSelectTheme = { theme -> viewModel.setCodeTheme(theme) },
                            autoInjectTailwind = autoInjectTailwind,
                            onToggleAutoInject = { viewModel.toggleAutoInjectTailwind() },
                            onCloseDrawer = {
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = {
                                    Column {
                                        Text(
                                            text = activeFile?.title ?: "HTML Editor",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Offline Tailwind CSS Enabled",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                },
                                navigationIcon = {
                                    IconButton(
                                        onClick = {
                                            scope.launch { drawerState.open() }
                                        },
                                        modifier = Modifier.testTag("open_drawer_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menu Projects"
                                        )
                                    }
                                },
                                actions = {
                                    // Device Viewport Emulation Dropdown
                                    Box {
                                        IconButton(
                                            onClick = { showViewportMenu = true },
                                            modifier = Modifier.testTag("viewport_menu_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Phonelink,
                                                contentDescription = "Device Viewport"
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = showViewportMenu,
                                            onDismissRequest = { showViewportMenu = false }
                                        ) {
                                            DeviceViewport.entries.forEach { vp ->
                                                DropdownMenuItem(
                                                    text = { Text(vp.label) },
                                                    onClick = {
                                                        viewModel.setDeviceViewport(vp)
                                                        showViewportMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    // Cycle Split Mode button
                                    IconButton(
                                        onClick = {
                                            val nextMode = when (splitMode) {
                                                SplitMode.SPLIT_HORIZONTAL -> SplitMode.SPLIT_VERTICAL
                                                SplitMode.SPLIT_VERTICAL -> SplitMode.EDITOR_ONLY
                                                SplitMode.EDITOR_ONLY -> SplitMode.PREVIEW_ONLY
                                                SplitMode.PREVIEW_ONLY -> SplitMode.SPLIT_HORIZONTAL
                                            }
                                            viewModel.setSplitMode(nextMode)
                                        },
                                        modifier = Modifier.testTag("top_split_mode_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Splitscreen,
                                            contentDescription = "Toggle View Mode"
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            // Find and Replace Bar if visible
                            AnimatedVisibility(visible = isFindReplaceVisible) {
                                FindReplaceBar(
                                    searchQuery = searchQuery,
                                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                    replaceQuery = replaceQuery,
                                    onReplaceQueryChange = { viewModel.setReplaceQuery(it) },
                                    onReplaceAll = { viewModel.replaceAll() },
                                    onClose = { viewModel.toggleFindReplace() }
                                )
                            }

                            // Split Screen Content Area
                            SplitScreenContainer(
                                splitMode = splitMode,
                                splitRatio = splitRatio,
                                onSplitRatioChange = { newRatio -> viewModel.setSplitRatio(newRatio) },
                                editorContent = { modifier ->
                                    CodeEditor(
                                        codeContent = codeContent,
                                        onCodeChange = { updated -> viewModel.updateCodeContent(updated) },
                                        theme = codeTheme,
                                        modifier = modifier,
                                        onCursorPositionChanged = { pos -> currentCursorPos = pos }
                                    )
                                },
                                previewContent = { modifier ->
                                    WebViewPreview(
                                        codeContent = codeContent,
                                        deviceViewport = deviceViewport,
                                        autoInjectTailwind = autoInjectTailwind,
                                        onConsoleLog = { log -> viewModel.addConsoleLog(log) },
                                        modifier = modifier
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )

                            // JS Dev Console Drawer (Collapsible)
                            AnimatedVisibility(visible = isConsoleOpen) {
                                DevConsoleView(
                                    logs = consoleLogs,
                                    onClearLogs = { viewModel.clearConsoleLogs() },
                                    onCloseConsole = { viewModel.toggleConsole() }
                                )
                            }

                            // Bottom Keyboard Quick Insertion Toolbar
                            QuickToolbar(
                                onInsertText = { textToInsert ->
                                    val (newText, newPos) = viewModel.insertTextAtCursor(
                                        textToInsert,
                                        codeContent,
                                        currentCursorPos
                                    )
                                    currentCursorPos = newPos
                                },
                                splitMode = splitMode,
                                onCycleSplitMode = {
                                    val next = when (splitMode) {
                                        SplitMode.SPLIT_HORIZONTAL -> SplitMode.SPLIT_VERTICAL
                                        SplitMode.SPLIT_VERTICAL -> SplitMode.EDITOR_ONLY
                                        SplitMode.EDITOR_ONLY -> SplitMode.PREVIEW_ONLY
                                        SplitMode.PREVIEW_ONLY -> SplitMode.SPLIT_HORIZONTAL
                                    }
                                    viewModel.setSplitMode(next)
                                },
                                onOpenSnippets = { showSnippetSheet = true },
                                onOpenColorPicker = { showColorPickerSheet = true },
                                onFormatCode = { viewModel.formatCode() },
                                onToggleConsole = { viewModel.toggleConsole() },
                                consoleLogCount = consoleLogs.size,
                                onUndo = { viewModel.undo() },
                                onRedo = { viewModel.redo() },
                                onToggleFindReplace = { viewModel.toggleFindReplace() }
                            )
                        }

                        // Code Snippets Modal Sheet
                        if (showSnippetSheet) {
                            SnippetSheet(
                                onSelectSnippet = { snippet ->
                                    viewModel.insertSnippet(snippet.code)
                                },
                                onDismiss = { showSnippetSheet = false }
                            )
                        }

                        // Color Swatch Picker Sheet
                        if (showColorPickerSheet) {
                            ColorPickerSheet(
                                onSelectColorClass = { colorClass ->
                                    viewModel.insertTextAtCursor(colorClass, codeContent, currentCursorPos)
                                },
                                onDismiss = { showColorPickerSheet = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
