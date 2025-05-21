// app/src/main/java/com/example/moso/ui/screens/order/OrderDetailScreen.kt
package com.example.moso.ui.screens.order

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moso.data.repository.ProductRepository
import com.example.moso.ui.theme.QuicksandFontFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    navController: NavController
) {
    data class OrderProduct(
        val id: String,
        val name: String,
        val imageUrl: String,
        val seller: String
    )

    var products by remember { mutableStateOf<List<OrderProduct>>(emptyList()) }
    var date by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val firestore = FirebaseFirestore.getInstance()
    val productRepo = remember { ProductRepository() }
    val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(orderId) {
        if (uid == null) {
            error = "Usuario no autenticado"
            isLoading = false
            return@LaunchedEffect
        }
        try {
            // 1️⃣ Obtén documento de la orden
            val orderDoc = firestore
                .collection("orders")
                .document(uid)
                .collection("userOrders")
                .document(orderId)
                .get()
                .await()

            // 2️⃣ Extrae la fecha
            date = orderDoc.getLong("timestamp")
                ?.let { dateFmt.format(Date(it)) }
                .orEmpty()

            // 3️⃣ Obtén lista inicial de pares (productId, qty) – aquí sólo importa el productId
            @Suppress("UNCHECKED_CAST")
            val raw = orderDoc.get("products") as? List<Map<String, Any>> ?: emptyList()

            // 4️⃣ Carga en paralelo cada producto completo
            val loaded = coroutineScope {
                raw.mapNotNull { item ->
                    async {
                        val pid = item["productId"] as? String ?: return@async null
                        // Llamada al repo para obtener Product
                        productRepo.getProductById(pid)
                            .getOrNull()
                            ?.let { prod ->
                                OrderProduct(
                                    id        = prod.id,
                                    name      = prod.name,
                                    imageUrl  = prod.imageUrl,
                                    seller    = prod.sellerName.ifBlank { "Desconocido" }
                                )
                            }
                    }
                }.awaitAll().filterNotNull()
            }

            products = loaded
            error = null
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de compra", fontFamily = QuicksandFontFamily) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                error != null -> {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }
                products.isEmpty() -> {
                    Text("No hay productos en esta orden")
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text("Fecha: $date", style = MaterialTheme.typography.bodyMedium)
                            Divider(Modifier.padding(vertical = 8.dp))
                        }
                        items(products) { p ->
                            Card(
                                elevation = CardDefaults.cardElevation(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = p.imageUrl.ifEmpty { "https://via.placeholder.com/60" },
                                        contentDescription = p.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(60.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(p.name, style = MaterialTheme.typography.bodyLarge)
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "Vendedor: ${p.seller}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
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




