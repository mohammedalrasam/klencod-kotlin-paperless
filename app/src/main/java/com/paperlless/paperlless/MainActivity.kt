@file:OptIn(ExperimentalMaterial3Api::class)

package com.paperlless.paperless

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paperlless.paperless.data.Document
import com.paperlless.paperless.data.DocumentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: DocumentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.google.android.gms.ads.MobileAds.initialize(this) {}
        setContent {
            PaperlessTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PaperlessTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF26A69A),
            primaryContainer = Color(0xFFB2DFDB),
            onPrimary = Color.White
        ),
        content = content
    )
}

@Composable
fun HomeScreen(viewModel: DocumentViewModel) {
    val documents by viewModel.documents.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paperless", fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* سنربطه بالماسح لاحقًا */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "مستند جديد", tint = Color.White)
            }
        }
    ) { padding ->
        if (documents.isEmpty()) {
            EmptyState(modifier = Modifier.padding(padding))
        } else {
            DocumentList(
                documents = documents,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Text(
                "لا توجد مستندات بعد",
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "اضغط زر + لبدء أول مستند",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun DocumentList(documents: List<Document>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(documents, key = { it.id }) { doc ->
            DocumentCard(doc)
        }
    }
}

@Composable
fun DocumentCard(document: Document) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = Color(0xFFE53935)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    document.name,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    formatDate(document.createdAt),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd – HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}