// [S17 AUTO-REPAIRED FOR GALAXY S17 / ONE UI 7]
package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "html_files")
data class HtmlFile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isTemplate: Boolean = false,
    val category: String = "General"
)