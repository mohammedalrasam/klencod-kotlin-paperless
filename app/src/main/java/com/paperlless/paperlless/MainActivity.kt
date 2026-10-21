@file:OptIn(ExperimentalMaterial3Api::class)

package com.paperlless.paperless

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.paperlless.paperless.data.Document
import com.paperlless.paperless.data.DocumentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: DocumentViewModel by viewModels()

    private val scannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(intent)
                val pdfUri = scanResult?.pdf?.uri
                if (pdfUri != null) {
                    lifecycleScope.launch {
                        saveScannedDocument(pdfUri)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.google.android.gms.ads.MobileAds.initialize(this) {}
        setContent {
            PaperlessTheme {
                HomeScreen(
                    viewModel = viewModel,
                    onScanClick = { launchScanner() }
                )
            }
        }
    }

    private fun launchScanner() {
        val options = GmsDocumentScannerOptions.Builder()
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .setGalleryImportAllowed(true)
            .setPageLimit(10)
            .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
            .build()

        val scanner = GmsDocumentScanning.getClient(options)

        scanner.getStartScanIntent(this)
            .addOnSuccessListener { intentSender ->
                scannerLauncher.launch(
                    IntentSenderRequest.Builder(intentSender).build()
                )
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "فشل فتح الماسح: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private suspend fun saveScannedDocument(uri: Uri) {
        try {
            val savedFile = withContext(Dispatchers.IO) {
                val docsDir = File(filesDir, "scanned_docs")
                if (!docsDir.exists()) docsDir.mkdirs()

                val timestamp = System.currentTimeMillis()
                val outputFile = File(docsDir, "scan_$timestamp.pdf")

                contentResolver.openInputStream(uri)?.use { input ->
                    outputFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                } ?: throw IllegalStateException("تعذر فتح الملف الممسوح")

                outputFile
            }

            val now = System.currentTimeMillis()
            val document = Document(
                name = "مستند ${formatDate(now)}",
                filePath = savedFile.absolutePath,
                createdAt = now
            )

            viewModel.addDocument(document)

            Toast.makeText(
                this,
                "✅ تم حفظ المستند بنجاح",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "خطأ في الحفظ: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
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
fun HomeScreen(
    viewModel: DocumentViewModel,
    onScanClick: () -> Unit
) {
    val documents by viewModel.documents.collectAsState()

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
                onClick = onScanClick,
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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