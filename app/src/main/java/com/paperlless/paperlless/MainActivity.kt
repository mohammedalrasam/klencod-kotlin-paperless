@file:OptIn(ExperimentalMaterial3Api::class)

package com.paperlless.paperless

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // تهيئة AdMob (بديل عن PaperlessApp)
        com.google.android.gms.ads.MobileAds.initialize(this) {}

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF26A69A),
                    primaryContainer = Color(0xFFB2DFDB)
                )
            ) {
                AppNav()
            }
        }
    }
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "home") {
        composable("home") {
            HomePlaceholder()
        }
    }
}

@Composable
fun HomePlaceholder() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paperless", fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "النسخة الأولية تعمل بنجاح ✅\nسنضيف الشاشات تباعًا",
                fontSize = 18.sp
            )
        }
    }
}