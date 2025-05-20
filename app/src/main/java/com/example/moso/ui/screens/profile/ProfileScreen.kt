package com.example.moso.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moso.ui.components.AboutCard
import com.example.moso.ui.components.ProfileHeader
import com.example.moso.ui.components.RecentOrdersCard
import com.example.moso.ui.components.StatCard
import com.example.moso.ui.theme.QuicksandFontFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// Colores de MOSO
val MosoBlue = Color(0xFF073763)
val MosoBrown = Color(0xFF966441)
val MosoGray = Color(0xFFE8E8E8)
val MosoWhite = Color(0xFFFFFFFF)
val MosoBlueDark = Color(0xFF173244)

// Modelo para órdenes recientes
data class RecentOrder(val id: String, val date: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateUp: () -> Unit
) {
    var productsCount by remember { mutableStateOf(0) }
    var purchasesCount by remember { mutableStateOf(0) }
    var salesCount by remember { mutableStateOf(0) }
    var recentOrders by remember { mutableStateOf<List<RecentOrder>>(emptyList()) }

    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid
    val db = FirebaseFirestore.getInstance()
    val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(uid) {
        uid?.let { userId ->
            val prodSnap = db.collection("products")
                .whereEqualTo("sellerId", userId)
                .get().await()
            productsCount = prodSnap.size()

            val ordersSnap = db.collection("orders")
                .document(userId)
                .collection("userOrders")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await()
            purchasesCount = ordersSnap.size()

            salesCount = 0 // placeholder

            recentOrders = ordersSnap.documents.take(3).mapNotNull { doc ->
                val ts = doc.getLong("timestamp") ?: return@mapNotNull null
                RecentOrder(doc.id, dateFmt.format(Date(ts)))
            }
        }
    }

    Scaffold(
        containerColor = MosoGray,
        topBar = {
            TopAppBar(
                title = { Text("Perfil", fontFamily = QuicksandFontFamily, fontWeight = FontWeight.Bold, color = MosoWhite) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = MosoWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MosoBlue)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ProfileHeader(
                    name  = user?.displayName ?: user?.email?.substringBefore('@').orEmpty(),
                    email = user?.email.orEmpty()
                )
            }
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(Icons.Outlined.Inventory2, productsCount.toString(), "Publicaciones", Modifier.weight(1f))
                    StatCard(Icons.Default.ShoppingCart, purchasesCount.toString(), "Compras",      Modifier.weight(1f))
                    StatCard(Icons.Default.Receipt, salesCount.toString(),    "Ventas",         Modifier.weight(1f))
                }

            }
            item { AboutCard() }
            item { RecentOrdersCard(recentOrders) }
        }
    }
}



