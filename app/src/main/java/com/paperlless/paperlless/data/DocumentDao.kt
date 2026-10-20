package com.paperlless.paperless.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Document>>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getById(id: Long): Document?

    @Insert
    suspend fun insert(document: Document): Long

    @Update
    suspend fun update(document: Document)

    @Delete
    suspend fun delete(document: Document)
}