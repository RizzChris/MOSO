package com.example.moso.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.moso.R
import com.example.moso.data.model.Product
import com.example.moso.data.model.ProductCategories
import com.example.moso.ui.theme.AccentOrange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.text.Normalizer
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSell: () -> Unit,
    onNavigateToCatalog: (String) -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToChat: () -> Unit,
) {
    // — Datos y carga de productos (igual que antes) —
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedCategory) {
        isLoading = true
        try {
            val query = FirebaseFirestore.getInstance().collection("products")
                .let { if (selectedCategory.isBlank()) it else it.whereEqualTo("categoryId", selectedCategory) }
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

    val countsByCat = remember(products) { products.groupingBy { it.categoryId }.eachCount() }

    // — Mapa de categoría a recurso drawable —
    val categoryImageMap = mapOf(
        "RESISTENCIAS" to R.drawable.resistencias,
        "CAPACITORES" to R.drawable.capacitores,
        "CIRCUITOS INTEGRADOS" to R.drawable.circuitos_integrados,
        "TRANSISTORES" to R.drawable.transistores,
        "DIODOS" to R.drawable.diodos,
        "SENSORES" to R.drawable.sensores,
        "DISPLAYS" to R.drawable.displays,
        "CONECTORES" to R.drawable.conectores,
        "FUENTES DE ALIMENTACION" to R.drawable.fuentes_de_alimentacion,
        "OTROS" to R.drawable.otro
    )

    // — Datos del carrusel —
    // Creamos una lista infinita duplicando los elementos para el efecto de carrusel infinito
    val originalCarouselData = listOf(
        Triple("RESISTENCIAS", R.drawable.resistencias, "¡Las resistencias limitan el flujo de corriente!"),
        Triple("CAPACITORES", R.drawable.capacitores, "¡Los capacitores almacenan energía en un campo eléctrico!"),
        Triple("CIRCUITOS INTEGRADOS", R.drawable.circuitos_integrados, "¡Varios componentes en un solo chip!"),
        Triple("TRANSISTORES", R.drawable.transistores, "¡Los transistores amplifican señales!"),
        Triple("DIODOS", R.drawable.diodos, "¡Los diodos permiten corriente unidireccional!"),
        Triple("SENSORES", R.drawable.sensores, "¡Detectan cambios en el entorno!"),
        Triple("DISPLAYS", R.drawable.displays, "¡Muestran información visual!"),
        Triple("CONECTORES", R.drawable.conectores, "¡Conectan circuitos eléctricos!"),
        Triple("FUENTES DE ALIMENTACION", R.drawable.fuentes_de_alimentacion, "¡Proporcionan energía!"),
    )

    // Duplicamos la lista para crear efecto de carrusel infinito
    val carouselData = originalCarouselData + originalCarouselData + originalCarouselData

    // Control de scroll automático
    val listState = rememberLazyListState()
    var currentPage by remember { mutableStateOf(originalCarouselData.size) } // Empezamos en la segunda "sección"

    // Iniciamos el scroll en la posición del segundo conjunto de items
    LaunchedEffect(Unit) {
        listState.scrollToItem(originalCarouselData.size)
    }

    LaunchedEffect(carouselData.size) {
        while (true) {
            delay(3000) // Aumentamos el tiempo para que sea más visible
            currentPage = (currentPage + 1) % carouselData.size
            listState.animateScrollToItem(currentPage)

            // Si llegamos al final del segundo conjunto, saltamos al inicio del segundo conjunto
            if (currentPage >= originalCarouselData.size * 2) {
                delay(500) // Pequeña pausa para que termine la animación
                currentPage = originalCarouselData.size
                listState.scrollToItem(currentPage)
            }
            // Si llegamos al inicio del segundo conjunto, saltamos al final del primer conjunto
            else if (currentPage < originalCarouselData.size) {
                delay(500) // Pequeña pausa para que termine la animación
                currentPage = originalCarouselData.size * 2 - 1
                listState.scrollToItem(currentPage)
            }
        }
    }

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

        // 2) Catálogo marrón
        Surface(
            color = Color(0xFF8B5E3C),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onNavigateToCatalog("") }
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

        // 3) Carrusel con "peek" simétrico a ambos lados y tamaño ajustado
        LazyRow(
            state = listState,
            // Cálculo preciso del padding para mantener simetría
            contentPadding = PaddingValues(
                start = (LocalConfiguration.current.screenWidthDp * 0.15).dp,
                end = (LocalConfiguration.current.screenWidthDp * 0.15).dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp), // Espacio entre items
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp) // Alto ajustado
                .padding(vertical = 8.dp)
        ) {
            items(carouselData) { (cat, img, desc) ->
                Card(
                    modifier = Modifier
                        .width((LocalConfiguration.current.screenWidthDp * 0.7).dp) // Ancho fijo calculado
                        .border(width = 1.dp, color = Color.LightGray),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .clickable { onNavigateToCatalog(cat) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(img),
                            contentDescription = cat,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        Spacer(Modifier.height(4.dp)) // Reducido
                        Text(
                            desc,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .fillMaxWidth()
                        )
                        Spacer(Modifier.height(4.dp)) // Reducido
                    }
                }
            }
        }

        // 4) Título "Categorías"
        Text(
            "Categorías",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
        )

        // 5) Grid de categorías con imágenes más grandes
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
                // Función auxiliar para quitar tildes
                fun String.removeAccents(): String =
                    Normalizer.normalize(this, Normalizer.Form.NFD)
                        .replace(Regex("\\p{M}"), "")

                items(ProductCategories.allCategories) { cat ->
                    // 1) preparar clave
                    val key = cat
                        .trim()
                        .removeAccents()
                        .uppercase(Locale.getDefault())
                val count = countsByCat[cat] ?: 0
                val imgRes = categoryImageMap[cat.uppercase()] ?: R.drawable.placeholder

                Card(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable { onNavigateToCatalog(cat.uppercase()) },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF2ECF7))
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(0.dp),  // ya no necesitamos padding aquí
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        // 1) Imagen ocupa todo el espacio posible
                        Image(
                            painter = painterResource(id = imgRes),
                            contentDescription = cat,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )

                        // 2) Pie de texto con fondo blanco y esquinas redondeadas
                        // 2) Pie de texto con AutoSizeText para que el nombre siempre quepa en una línea
                        Surface(
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            tonalElevation = 2.dp,
                            shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                        ) {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 6.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = cat.uppercase(),
                                    modifier = Modifier.fillMaxWidth(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                                if (count > 0) {
                                    Text(
                                        text = "$count productos",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AccentOrange,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }
    }
    }


