package com.example.moso.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.moso.R
import com.example.moso.data.model.Product
import com.example.moso.data.model.ProductCategories
import com.example.moso.ui.theme.AccentOrange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSell: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToCatalog: (String) -> Unit,
) {
    // — Datos y carga —
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedCategory) {
        scope.launch {
            isLoading = true
            try {
                val query = FirebaseFirestore.getInstance()
                    .collection("products")
                    .let {
                        if (selectedCategory.isBlank()) it
                        else it.whereEqualTo("categoryId", selectedCategory)
                    }
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

    val countsByCat = remember(products) { products.groupingBy { it.categoryId }.eachCount() }

    // — Mapa de categoría a recurso drawable —
    val categoryImageMap = mapOf(
        "RESISTENCIAS"           to R.drawable.resistencias,
        "CAPACITORES"            to R.drawable.capacitores,
        "CIRCUITOS INTEGRADOS"   to R.drawable.circuitos_integrados,
        "TRANSISTORES"           to R.drawable.transistores,
        "DIODOS"                 to R.drawable.diodos,
        "SENSORES"               to R.drawable.sensores,
        "DISPLAYS"               to R.drawable.displays,
        "CONECTORES"             to R.drawable.conectores,
        "FUENTES DE ALIMENTACION" to R.drawable.fuentes_de_alimentacion,
        "Otro"                   to R.drawable.otro
    )

    // — Datos del carrusel —
    val categoryData = listOf(
        Triple("RESISTENCIAS", R.drawable.resistencias, "¡Las resistencias limitan el flujo de corriente en un circuito!"),
        Triple("CAPACITORES", R.drawable.capacitores, "¡Los capacitores almacenan energía en un campo eléctrico!"),
        Triple("CIRCUITOS INTEGRADOS", R.drawable.circuitos_integrados, "¡Los circuitos integrados combinan múltiples componentes electrónicos en un solo chip!"),
        Triple("TRANSISTORES", R.drawable.transistores, "¡Los transistores amplifican señales eléctricas!"),
        Triple("DIODOS", R.drawable.diodos, "¡Los diodos permiten que la corriente fluya en una sola dirección!"),
        Triple("SENSORES", R.drawable.sensores, "¡Los sensores detectan cambios en el entorno físico!"),
        Triple("DISPLAYS", R.drawable.displays, "¡Los displays son pantallas que muestran información visual!"),
        Triple("CONECTORES", R.drawable.conectores, "¡Los conectores permiten la interconexión de circuitos eléctricos!"),
        Triple("FUENTES DE ALIMENTACION", R.drawable.fuentes_de_alimentacion, "¡Las fuentes de alimentación proporcionan energía a los circuitos!")
    )

    // — Contenido —
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1) Vender Componente
        TextButton(
            onClick = onNavigateToSell,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Vender Componente", style = MaterialTheme.typography.titleMedium)
        }

        // 2) Sección marrón Catálogo
        Surface(
            color = Color(0xFF8B5E3C),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = { onNavigateToCatalog("") })
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Catálogo",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // 3) Carrusel de imágenes con datos interesantes
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categoryData) { category ->
                val (name, imageRes, description) = category
                Card(
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp)
                        .clickable {
                            onNavigateToCatalog(name) // Redirige a CatalogScreen con la categoría
                        },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Imagen de la categoría
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .padding(8.dp)
                        )

                        // Texto interesante de la categoría
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 4) Título Categorías
        Text(
            "Categorías",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        // 5) Grid de categorías con imágenes
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ProductCategories.allCategories) { cat ->
                val count = countsByCat[cat] ?: 0
                val imageRes = categoryImageMap[cat.uppercase()] ?: R.drawable.placeholder

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.85f) // Más alargado vertical
                        .height(160.dp)     // Más alto
                        .clickable {
                            onNavigateToCatalog(cat.uppercase()) // Redirige a CatalogScreen con la categoría
                        }
                        .padding(4.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF2ECF7))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 10.dp, bottom = 8.dp, start = 4.dp, end = 4.dp), // Menos margen
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = cat,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(72.dp) // Un poco más grande y centrada
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = cat.uppercase(),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (count > 0) {
                            Text(
                                text = "$count productos",
                                style = MaterialTheme.typography.bodySmall,
                                color = AccentOrange,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}





