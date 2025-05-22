package com.example.moso.ui.screens.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moso.data.model.Product
import com.example.moso.data.repository.ProductRepository
import com.example.moso.ui.navigation.Screen
import com.example.moso.ui.theme.QuicksandFontFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = FirebaseFirestore.getInstance()
    val productRepo = remember { ProductRepository() }
    val scope = rememberCoroutineScope()

    data class CartItemDetail(val product: Product, val quantity: Int)

    var items by remember { mutableStateOf<List<CartItemDetail>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    // Carga el carrito y sus productos
    LaunchedEffect(uid) {
        try {
            val cartDoc = db.collection("cart").document(uid).get().await()
            @Suppress("UNCHECKED_CAST")
            val raw = cartDoc.get("products") as? List<Map<String, Any>> ?: emptyList()

            val loaded = coroutineScope {
                raw.mapNotNull { entry ->
                    async {
                        val pid = entry["productId"] as? String ?: return@async null
                        val qty = (entry["quantity"] as? Number)?.toInt() ?: return@async null
                        productRepo.getProductById(pid).getOrNull()?.let { prod ->
                            CartItemDetail(prod, qty)
                        }
                    }
                }.awaitAll().filterNotNull()
            }
            items = loaded
            error = null
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    // Total
    val total by remember(items) { mutableStateOf(items.sumOf { it.product.price * it.quantity }) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito", fontFamily = QuicksandFontFamily) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            if (items.isNotEmpty()) {
                Column {
                    Text(
                        "Total: $${"%.2f".format(total)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = QuicksandFontFamily,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        textAlign = TextAlign.End
                    )
                    Button(
                        onClick = {
                            scope.launch {
                                isProcessing = true
                                try {
                                    // 1) Nuevo ID y payload
                                    val orderId = UUID.randomUUID().toString()
                                    val orderData = mapOf(
                                        "timestamp" to System.currentTimeMillis(),
                                        "products" to items.map { detail ->
                                            mapOf(
                                                "productId" to detail.product.id,
                                                "quantity" to detail.quantity
                                            )
                                        },
                                        "total" to total
                                    )
                                    // 2) Guarda la orden
                                    db.collection("orders")
                                        .document(uid)
                                        .collection("userOrders")
                                        .document(orderId)
                                        .set(orderData)
                                        .await()
                                    // 3) Vacía solo el array products
                                    db.collection("cart")
                                        .document(uid)
                                        .update("products", emptyList<Map<String, Any>>())
                                        .await()
                                    // 4) Navega a ProcessingScreen
                                    navController.navigate(Screen.Processing.route) {
                                        popUpTo(Screen.Home.route) { inclusive = false }
                                    }
                                } catch (e: Exception) {
                                    error = e.message
                                } finally {
                                    isProcessing = false
                                }
                            }
                        },
                        enabled = !isProcessing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Realizar compra — $${"%.2f".format(total)}")
                        }
                    }
                }
            }
        }
    ) { inner ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Text(
                    "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                items.isEmpty() -> Text(
                    "Tu carrito está vacío",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(items) { (prod, qty) ->
                        CartItemRow(prod, qty)
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(product: Product, quantity: Int) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl.ifEmpty { "https://via.placeholder.com/60" },
                contentDescription = product.name,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.bodyLarge)
                Text("Cantidad: $quantity", style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                "$${"%.2f".format(product.price * quantity)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}



/**
 * Detalle de ítem del carrito con imagen y vendedor
 */
data class CartItemDetail(
    val productId: String,
    val sellerName: String,
    val imageUrl: String,
    val quantity: Int
)
