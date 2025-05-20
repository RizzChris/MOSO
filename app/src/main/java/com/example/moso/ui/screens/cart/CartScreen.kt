package com.example.moso.ui.screens.cart

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moso.ui.navigation.Screen
import com.example.moso.ui.theme.QuicksandFontFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()

    // Estado para ítems detallados y UI
    val cartItems = remember { mutableStateListOf<CartItemDetail>() }
    var isLoading by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Carga inicial: lee IDs y cantidades, luego detalles de cada producto
    LaunchedEffect(userId) {
        if (userId == null) {
            errorMessage = "Usuario no autenticado"
            isLoading = false
        } else {
            try {
                val cartSnap = firestore.collection("cart").document(userId).get().await()
                val products = cartSnap.get("products") as? List<Map<String, Any>>
                cartItems.clear()
                products?.forEach { map ->
                    val pid = map["productId"] as String
                    val qty = (map["quantity"] as Long).toInt()

                    // Obtener detalles del producto
                    val prodSnap = firestore.collection("products").document(pid).get().await()
                    val sellerName = prodSnap.getString("sellerName") ?: ""
                    val imageUrl = prodSnap.getString("imageUrl") ?: ""

                    cartItems.add(
                        CartItemDetail(
                            productId = pid,
                            sellerName = sellerName,
                            imageUrl = imageUrl,
                            quantity = qty
                        )
                    )
                }
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

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
            if (cartItems.isNotEmpty()) {
                Button(
                    onClick = {
                        scope.launch {
                            isProcessing = true
                            errorMessage = null
                            try {
                                val uid = userId!!
                                val timestamp = System.currentTimeMillis()

                                // Crear orden en Firestore
                                val orderRef = firestore.collection("orders")
                                    .document(uid)
                                    .collection("userOrders")
                                    .document()
                                val orderData = mapOf(
                                    "timestamp" to timestamp,
                                    "products" to cartItems.map { ci ->
                                        mapOf(
                                            "productId" to ci.productId,
                                            "quantity" to ci.quantity
                                        )
                                    }
                                )
                                orderRef.set(orderData).await()

                                // Batch: restar stock y borrar carrito
                                val batch = firestore.batch()
                                cartItems.forEach { ci ->
                                    val prodRef = firestore.collection("products").document(ci.productId)
                                    batch.update(prodRef, "stock", FieldValue.increment(-ci.quantity.toLong()))
                                }
                                val cartRef = firestore.collection("cart").document(uid)
                                batch.delete(cartRef)
                                batch.commit().await()

                                // Navegar a historial
                                navController.navigate(Screen.Purchases.route) {
                                    popUpTo(Screen.Home.route) { inclusive = false }
                                }
                            } catch (e: Exception) {
                                errorMessage = e.message
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
                        Text("Realizar compra")
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                errorMessage != null -> Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                cartItems.isEmpty() -> Text(
                    "Tu carrito está vacío",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemCard(item)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(item: CartItemDetail) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl.ifEmpty { "https://via.placeholder.com/60" },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "Vendedor: ${item.sellerName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Cantidad: ${item.quantity}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
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
