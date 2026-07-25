// [S17 AUTO-REPAIRED FOR GALAXY S17 / ONE UI 7]
package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HtmlFileDao {
    @Query("SELECT * FROM html_files ORDER BY updatedAt DESC")
    fun getAllFiles(): Flow<List<HtmlFile>>

    @Query("SELECT * FROM html_files WHERE id = :id")
    suspend fun getFileById(id: Long): HtmlFile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: HtmlFile): Long

    @Update
    suspend fun updateFile(file: HtmlFile)

    @Query("DELETE FROM html_files WHERE id = :id")
    suspend fun deleteFileById(id: Long)

    @Query("SELECT COUNT(*) FROM html_files")
    suspend fun getFileCount(): Int
}