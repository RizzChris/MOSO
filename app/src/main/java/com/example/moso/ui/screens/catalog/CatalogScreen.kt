// app/src/main/java/com/example/moso/ui/screens/catalog/CatalogScreen.kt

package com.example.moso.ui.screens.catalog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moso.data.model.Product
import com.example.moso.ui.components.ProductCard
import com.example.moso.ui.navigation.Screen
import com.example.moso.ui.theme.QuicksandFontFamily
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(navController: NavController, categoryId: String?) {
    val scope = rememberCoroutineScope()
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(categoryId) {
        isLoading = true
        try {
            val query = FirebaseFirestore.getInstance()
                .collection("products")
                .let { if (!categoryId.isNullOrBlank()) it.whereEqualTo("categoryId", categoryId) else it }
            val snapshot = query.get().await()
            products = snapshot.documents.mapNotNull {
                it.toObject(Product::class.java)?.copy(id = it.id)
            }
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Catálogo",
                        fontFamily = QuicksandFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                errorMessage != null -> Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                products.isEmpty() -> Text("No hay productos", modifier = Modifier.align(Alignment.Center))
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products) { product ->
                        ProductCard(product) {
                            navController.navigate(Screen.ProductDetail.createRoute(product.id))
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFF8B5E3C) else Color.LightGray.copy(alpha = 0.3f),
        contentColor = if (isSelected) Color.White else Color.Black,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}





