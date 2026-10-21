package com.paperlless.paperless.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DocumentViewModel(app: Application) : AndroidViewModel(app) {

    private val repository: DocumentRepository

    init {
        val db = AppDatabase.getInstance(app)
        repository = DocumentRepository(db.documentDao())
    }

    val documents: StateFlow<List<Document>> = repository.allDocuments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addDocument(document: Document) = viewModelScope.launch {
        repository.insert(document)
    }

    fun deleteDocument(document: Document) = viewModelScope.launch {
        repository.delete(document)
    }

    fun updateDocument(document: Document) = viewModelScope.launch {
        repository.update(document)
    }
}