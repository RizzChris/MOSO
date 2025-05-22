// ui/screens/posts/PostsScreen.kt
package com.example.moso.ui.screens.posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moso.data.model.Product
import com.example.moso.data.repository.ProductRepository
import com.example.moso.ui.components.ProductCard
import com.example.moso.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(navController: NavController) {
    val repo = remember { ProductRepository() }
    val uid = FirebaseAuth.getInstance().currentUser!!.uid
    var posts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uid) {
        isLoading = true
        posts = repo.getProductsBySeller(uid).getOrNull().orEmpty()
        isLoading = false
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis publicaciones") }) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error    != null -> Text(error!!, Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                posts.isEmpty()  -> Text("No tienes publicaciones", Modifier.align(Alignment.Center))
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(posts) { prod ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Aquí tu ProductCard (o los detalles que quieras mostrar)
                                ProductCard(prod) {
                                    navController.navigate(Screen.ProductDetail.createRoute(prod.id))
                                }

                                // Botones de acción
                                Row {
                                    IconButton(onClick = {
                                        // Navegar a pantalla de edición (tienes que crearla)
                                        navController.navigate(Screen.EditProduct.createRoute(prod.id))
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar publicación")
                                    }
                                    IconButton(onClick = {
                                        // Borrar en Firebase y actualizar lista local
                                        scope.launch {
                                            repo.deleteProduct(prod.id)
                                            posts = posts.filterNot { it.id == prod.id }
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar publicación")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



