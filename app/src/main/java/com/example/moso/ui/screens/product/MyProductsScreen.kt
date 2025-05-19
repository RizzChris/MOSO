package com.example.moso.ui.screens.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moso.data.model.Product
import com.example.moso.ui.components.ProductCard
import com.example.moso.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun MyProductsScreen(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val scope = rememberCoroutineScope()
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch products for the current user
    LaunchedEffect(userId) {
        if (userId != null) {
            scope.launch {
                isLoading = true
                try {
                    val query = FirebaseFirestore.getInstance()
                        .collection("products")
                        .whereEqualTo("sellerId", userId) // Filter by sellerId
                    val snapshot = query.get().await()
                    products = snapshot.documents
                        .mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) }
                    errorMessage = null
                } catch (e: Exception) {
                    errorMessage = e.message
                } finally {
                    isLoading = false
                }
            }
        }
    }

    // Show loading indicator or products
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            // Show error message if any
            errorMessage?.let {
                Text(text = "Error: $it", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }

            // Show products or a message if there are none
            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes productos publicados.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                    items(products) { product ->
                        ProductCard(
                            product = product,
                            onClick = { navController.navigate(Screen.ProductDetail.createRoute(product.id)) }
                        )
                    }
                }
            }
        }
    }
}