package com.example.moso.ui.screens.product

import android.annotation.SuppressLint
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moso.data.model.Product
import com.example.moso.data.repository.ProductRepository
import com.example.moso.ui.navigation.Screen
import com.example.moso.ui.theme.MosoBlue
import com.example.moso.ui.theme.QuicksandFontFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val productRepository = remember { ProductRepository() }

    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var quantity by remember { mutableIntStateOf(1) }
    var isAdding by remember { mutableStateOf(false) }
    var sellerName by remember { mutableStateOf("") }

    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Cargar detalles del producto
    LaunchedEffect(productId) {
        isLoading = true
        productRepository.getProductById(productId)
            .onSuccess { prod ->
                product = prod
                // Si tienes el sellerName guardado en el producto, úsalo
                sellerName = prod.sellerName.ifBlank { "" }
                isLoading = false
            }
            .onFailure {
                errorMessage = it.message
                isLoading = false
            }
    }

    // Si no había sellerName en el objeto, intenta cargar del perfil de usuario
    LaunchedEffect(product?.sellerId) {
        product?.sellerId?.let { sid ->
            try {
                val userDoc = firestore.collection("users").document(sid).get().await()
                sellerName = userDoc.getString("name")
                    ?: userDoc.getString("displayName")
                            ?: "Desconocido"
            } catch (_: Exception) {
                // deja lo que hubiera en sellerName
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Detalles del producto",
                        fontFamily = QuicksandFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        },
        bottomBar = {
            product?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Chat con vendedor
                    OutlinedButton(
                        onClick = {
                            navController.navigate(Screen.Chat.createRoute(it.sellerId))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Chat con vendedor",
                            fontFamily = QuicksandFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Agregar al carrito
                    Button(
                        onClick = {
                            scope.launch {
                                isAdding = true
                                try {
                                    val uid = userId ?: throw Exception("Inicia sesión para agregar al carrito")
                                    val cartRef = firestore.collection("cart").document(uid)
                                    val item = mapOf("productId" to it.id, "quantity" to quantity)
                                    cartRef.set(
                                        mapOf("products" to FieldValue.arrayUnion(item)),
                                        SetOptions.merge()
                                    ).await()
                                    // Navegar a carrito
                                    navController.navigate(Screen.Cart.route)
                                } catch (e: Exception) {
                                    errorMessage = e.message
                                } finally {
                                    isAdding = false
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        enabled = !isAdding && (product?.stock ?: 0) > 0
                    ) {
                        if (isAdding) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Agregar al carrito",
                                fontFamily = QuicksandFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center), color = MosoBlue)
                errorMessage != null -> Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                product != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = product!!.imageUrl.ifEmpty { "https://via.placeholder.com/400" },
                            contentDescription = product!!.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = product!!.name,
                                fontFamily = QuicksandFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Vendedor: $sellerName",
                                fontFamily = QuicksandFontFamily,
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = "Descripción",
                                fontFamily = QuicksandFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = product!!.description,
                                fontFamily = QuicksandFontFamily,
                                fontSize = 16.sp
                            )
                            Spacer(Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$${product!!.price}",
                                    fontFamily = QuicksandFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = MosoBlue
                                )
                                Text(
                                    text = "Stock: ${product!!.stock}",
                                    fontFamily = QuicksandFontFamily,
                                    fontSize = 16.sp,
                                    color = if (product!!.stock > 0) Color.Gray else Color.Red
                                )
                            }
                            Spacer(Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Cantidad:",
                                    fontFamily = QuicksandFontFamily,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                FilledIconButton(
                                    onClick = { if (quantity > 1) quantity-- },
                                    enabled = quantity > 1
                                ) { Text(text = "-") }
                                Text(
                                    text = "${quantity}",
                                    fontFamily = QuicksandFontFamily,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(40.dp)
                                )
                                FilledIconButton(
                                    onClick = { if (quantity < product!!.stock) quantity++ },
                                    enabled = quantity < product!!.stock
                                ) { Text(text = "+") }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Total: $${String.format("%.2f", product!!.price * quantity)}",
                                fontFamily = QuicksandFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MosoBlue
                            )
                            Spacer(Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}
