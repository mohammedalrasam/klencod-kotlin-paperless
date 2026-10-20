package com.paperlless.paperless.data

import kotlinx.coroutines.flow.Flow

class DocumentRepository(private val dao: DocumentDao) {
    val allDocuments: Flow<List<Document>> = dao.getAll()

    suspend fun insert(document: Document): Long = dao.insert(document)
    suspend fun update(document: Document) = dao.update(document)
    suspend fun delete(document: Document) = dao.delete(document)
    suspend fun getById(id: Long): Document? = dao.getById(id)
}