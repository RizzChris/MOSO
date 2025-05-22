package com.example.moso.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.navigation.NavController
import com.example.moso.ui.components.AboutCard
import com.example.moso.ui.components.ProfileHeader
import com.example.moso.ui.components.RecentOrdersCard
import com.example.moso.ui.components.StatCard
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
fun ProfileScreen(
    navController: NavController,
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
        if (uid == null) return@LaunchedEffect

        // 1️⃣ Publicaciones
        val prodSnap = db.collection("products")
            .whereEqualTo("sellerId", uid)
            .get().await()
        productsCount = prodSnap.size()

        // 2️⃣ Compras
        val ordersSnap = db.collection("orders")
            .document(uid)
            .collection("userOrders")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get().await()
        purchasesCount = ordersSnap.size()

        // 3️⃣ Ventas desde /sales/{uid}/salesList
        val salesSnap = db.collection("sales")
            .document(uid)
            .collection("salesList")
            .get().await()
        salesCount = salesSnap.size()

        // 4️⃣ Órdenes recientes (solo compras)
        recentOrders = ordersSnap.documents.take(3).mapNotNull { doc ->
            val ts = doc.getLong("timestamp") ?: return@mapNotNull null
            RecentOrder(doc.id, dateFmt.format(Date(ts)))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Perfil",
                        fontFamily = QuicksandFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF073763))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ProfileHeader(
                    name  = user?.displayName ?: user?.email.orEmpty().substringBefore('@'),
                    email = user?.email.orEmpty()
                )
            }
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        icon     = Icons.Outlined.Inventory2,
                        value    = productsCount.toString(),
                        label    = "Publicaciones",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navController.navigate(Screen.Posts.route) }
                    )
                    StatCard(
                        icon     = Icons.Default.ShoppingCart,
                        value    = productsCount.toString(),
                        label    = "Compras",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navController.navigate(Screen.Purchases.route) }
                    )
                    StatCard(
                        icon     = Icons.Default.Receipt,
                        value    = productsCount.toString(),
                        label    = "Ventas",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navController.navigate(Screen.Sales.route) }
                    )
                }
            }

            item { AboutCard() }
            item { RecentOrdersCard(recentOrders) }
        }
    }
}

// (fuera de ProfileScreen.kt)
data class RecentOrder(val id: String, val date: String)




