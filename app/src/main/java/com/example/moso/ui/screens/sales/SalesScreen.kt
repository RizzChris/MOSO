package com.example.moso.ui.screens.sales

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moso.ui.theme.QuicksandFontFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SoldProduct(
    val productId: String,
    val name: String,
    val imageUrl: String,
    val date: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    navController: NavController
) {
    var salesCount   by remember { mutableStateOf(0) }
    var recentSales  by remember { mutableStateOf<List<SoldProduct>>(emptyList()) }
    var isLoading    by remember { mutableStateOf(true) }
    var error        by remember { mutableStateOf<String?>(null) }

    val uid    = FirebaseAuth.getInstance().currentUser?.uid
    val db     = FirebaseFirestore.getInstance()
    val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(uid) {
        if (uid == null) {
            error = "Usuario no autenticado"
            isLoading = false
            return@LaunchedEffect
        }
        try {
            // 🔥 Leemos sólo la subcolección de ventas de este vendedor:
            val salesSnapshot = db
                .collection("sales")
                .document(uid)
                .collection("salesList")
                .get()
                .await()

            // Convertimos cada documento a SoldProduct:
            val list = salesSnapshot.documents.map { doc ->
                SoldProduct(
                    productId = doc.getString("productId")!!,
                    name      = doc.getString("name")     ?: "Sin nombre",
                    imageUrl  = doc.getString("imageUrl")  ?: "",
                    date      = dateFmt.format(Date(doc.getLong("timestamp") ?: 0L))
                )
            }

            salesCount   = list.size
            recentSales  = list.sortedByDescending { it.date }.take(3)
            error        = null
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ventas", fontFamily = QuicksandFontFamily) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "Total unidades vendidas: $salesCount",
                                style = MaterialTheme.typography.headlineSmall,
                                fontFamily = QuicksandFontFamily
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Ventas recientes",
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = QuicksandFontFamily
                            )
                        }
                        if (recentSales.isEmpty()) {
                            item {
                                Text(
                                    "No hay ventas recientes",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = QuicksandFontFamily,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            items(recentSales) { s ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = s.imageUrl.ifEmpty { "https://via.placeholder.com/60" },
                                            contentDescription = s.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Column {
                                            Text(s.name, style = MaterialTheme.typography.bodyLarge)
                                            Text("Fecha: ${s.date}", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

