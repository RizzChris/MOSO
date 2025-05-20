package com.example.moso.ui.screens.purchases

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moso.ui.navigation.Screen
import com.example.moso.ui.theme.QuicksandFontFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasesScreen(
    navController: NavController
) {
    data class OrderSummary(val id: String, val category: String, val date: String)

    var orders by remember { mutableStateOf<List<OrderSummary>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val firestore = FirebaseFirestore.getInstance()
    val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(userId) {
        if (userId == null) {
            error = "Usuario no autenticado"
        } else {
            try {
                val snap = firestore
                    .collection("orders")
                    .document(userId)
                    .collection("userOrders")
                    .orderBy("timestamp")
                    .get()
                    .await()

                orders = snap.documents.mapNotNull { doc ->
                    val ts = doc.getLong("timestamp") ?: return@mapNotNull null
                    val date = dateFmt.format(Date(ts))
                    // Leer categoría del primer producto de la orden
                    val raw = doc.get("products") as? List<Map<String, Any>> ?: emptyList()
                    val firstCat = if (raw.isNotEmpty()) {
                        firestore.collection("products")
                            .document(raw.first()["productId"] as String)
                            .get().await()
                            .getString("category")
                            ?: "Sin categoría"
                    } else "Sin productos"
                    OrderSummary(id = doc.id, category = firstCat, date = date)
                }.reversed()
                error = null
            } catch (e: Exception) {
                error = e.message
            }
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tus compras recientes", fontFamily = QuicksandFontFamily) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> Text("Cargando...", Modifier.align(Alignment.Center))
                error != null -> Text(text = error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                orders.isEmpty() -> Text("No has realizado compras aún", Modifier.align(Alignment.Center))
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(
                                        Screen.OrderDetail.createRoute(order.id)
                                    )
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Folio: ${order.id}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Compraste: ${order.category}", style = MaterialTheme.typography.bodySmall)
                                }
                                Text(order.date, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}


