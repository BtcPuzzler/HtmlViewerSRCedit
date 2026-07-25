package com.example.ui.projects

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.HtmlFile
import com.example.ui.editor.CodeTheme

@Composable
fun ProjectDrawerContent(
    projects: List<HtmlFile>,
    activeProject: HtmlFile?,
    onSelectProject: (HtmlFile) -> Unit,
    onCreateProject: (title: String, template: String) -> Unit,
    onDeleteProject: () -> Unit,
    onRenameProject: (String) -> Unit,
    currentTheme: CodeTheme,
    onSelectTheme: (CodeTheme) -> Unit,
    autoInjectTailwind: Boolean,
    onToggleAutoInject: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    val context = LocalContext.current
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var newProjectTitle by remember { mutableStateOf("") }
    var selectedTemplate by remember { mutableStateOf("TAILWIND_CARD") }

    ModalDrawerSheet(
        modifier = Modifier
            .width(320.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Projects & Settings",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Action: New Project Button
            Button(
                onClick = { showNewProjectDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_project_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Project")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Project Card Controls (Share, Rename, Delete)
            activeProject?.let { current ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Active: ${current.title}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            // Share / Export HTML
                            IconButton(onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, current.content)
                                    type = "text/html"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Export HTML File")
                                context.startActivity(shareIntent)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Export HTML",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Rename
                            IconButton(onClick = {
                                newProjectTitle = current.title
                                showRenameDialog = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Rename",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Delete
                            IconButton(onClick = onDeleteProject) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Saved Projects (${projects.size})",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            // Projects List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(projects) { project ->
                    val isSelected = project.id == activeProject?.id
                    Surface(
                        onClick = {
                            onSelectProject(project)
                            onCloseDrawer()
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("project_item_${project.title}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = project.title,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = project.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Editor Theme Selector
            Text(
                text = "Editor Theme",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CodeTheme.entries.forEach { theme ->
                    Surface(
                        onClick = { onSelectTheme(theme) },
                        shape = RoundedCornerShape(6.dp),
                        color = if (theme == currentTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = theme.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (theme == currentTheme) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Auto Inject Tailwind CSS
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Offline Tailwind CSS",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = autoInjectTailwind,
                    onCheckedChange = { onToggleAutoInject() },
                    modifier = Modifier.testTag("tailwind_switch")
                )
            }
        }
    }

    // New Project Dialog
    if (showNewProjectDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showNewProjectDialog = false },
            title = { Text("Create New Project") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newProjectTitle,
                        onValueChange = { newProjectTitle = it },
                        label = { Text("Project Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_project_name_field")
                    )

                    Text("Starter Template:", style = MaterialTheme.typography.labelMedium)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedTemplate = "TAILWIND_CARD" }
                    ) {
                        RadioButton(selected = selectedTemplate == "TAILWIND_CARD", onClick = { selectedTemplate = "TAILWIND_CARD" })
                        Text("Tailwind Component Card")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedTemplate = "BLANK" }
                    ) {
                        RadioButton(selected = selectedTemplate == "BLANK", onClick = { selectedTemplate = "BLANK" })
                        Text("Blank HTML5 Boilerplate")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCreateProject(newProjectTitle, selectedTemplate)
                        newProjectTitle = ""
                        showNewProjectDialog = false
                        onCloseDrawer()
                    },
                    modifier = Modifier.testTag("confirm_create_project")
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                Button(onClick = { showNewProjectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Rename Dialog
    if (showRenameDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Project") },
            text = {
                OutlinedTextField(
                    value = newProjectTitle,
                    onValueChange = { newProjectTitle = it },
                    label = { Text("New Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    onRenameProject(newProjectTitle)
                    showRenameDialog = false
                }) {
                    Text("Rename")
                }
            },
            dismissButton = {
                Button(onClick = { showRenameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
