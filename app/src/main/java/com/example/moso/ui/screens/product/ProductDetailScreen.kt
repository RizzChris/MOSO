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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moso.data.model.Product
import com.example.moso.data.repository.ProductRepository
import com.example.moso.ui.navigation.Screen
import com.example.moso.ui.theme.MosoBlue
import com.example.moso.ui.theme.QuicksandFontFamily
import kotlinx.coroutines.launch

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

    // Cargar detalles del producto
    LaunchedEffect(productId) {
        scope.launch {
            isLoading = true
            productRepository.getProductById(productId)
                .onSuccess { productDetails ->
                    product = productDetails
                    isLoading = false
                }
                .onFailure { error ->
                    errorMessage = error.message
                    isLoading = false
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MosoBlue
                )
            } else if (errorMessage != null) {
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (product != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Imagen del producto
                    AsyncImage(
                        model = product?.imageUrl?.ifEmpty { "https://via.placeholder.com/400" },
                        contentDescription = product?.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )

                    // Información del producto
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = product?.name ?: "",
                            fontFamily = QuicksandFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Vendedor: ${product?.sellerName}",
                            fontFamily = QuicksandFontFamily,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Descripción",
                            fontFamily = QuicksandFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = product?.description ?: "",
                            fontFamily = QuicksandFontFamily,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Precio y stock
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$${product?.price}",
                                fontFamily = QuicksandFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = MosoBlue
                            )

                            Text(
                                text = "Stock disponible: ${product?.stock ?: 0}",
                                fontFamily = QuicksandFontFamily,
                                fontSize = 16.sp,
                                color = if ((product?.stock ?: 0) > 0) Color.Gray else Color.Red
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Selector de cantidad
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
                            ) {
                                Text(text = "-")
                            }

                            Text(
                                text = "$quantity",
                                fontFamily = QuicksandFontFamily,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            FilledIconButton(
                                onClick = { if (quantity < (product?.stock ?: 0)) quantity++ },
                                enabled = quantity < (product?.stock ?: 0)
                            ) {
                                Text(text = "+")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Total
                        Text(
                            text = "Total: $${String.format("%.2f", (product?.price ?: 0.0) * quantity)}",
                            fontFamily = QuicksandFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MosoBlue
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Botones de acción
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Botón de chat
                            OutlinedButton(
                                onClick = {
                                    product?.sellerId?.let { sellerId ->
                                        navController.navigate(Screen.Chat.createRoute(sellerId))
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 12.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Chat con vendedor",
                                    fontFamily = QuicksandFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Botón de compra
                            Button(
                                onClick = {
                                    // TODO: Implementar lógica de compra
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MosoBlue
                                ),
                                enabled = (product?.stock ?: 0) > 0
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Comprar ahora",
                                    fontFamily = QuicksandFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}