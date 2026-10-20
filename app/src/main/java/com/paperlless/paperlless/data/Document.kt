package com.paperlless.paperless.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class Document(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val filePath: String,
    val headerText: String = "",
    val footerText: String = "",
    val createdAt: Long = System.currentTimeMillis()
)