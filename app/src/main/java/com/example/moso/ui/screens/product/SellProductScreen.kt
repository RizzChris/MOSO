package com.example.moso.ui.screens.product

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moso.data.model.Product
import com.example.moso.data.repository.ProductRepository
import com.example.moso.ui.navigation.Screen
import com.example.moso.ui.theme.MosoBlue
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID

@Suppress("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellProductScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val productRepository = remember { ProductRepository() }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") } // Nueva variable para descripción
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val isFormValid = name.isNotBlank() && description.isNotBlank() &&
            price.toDoubleOrNull() != null && stock.toIntOrNull() != null && category.isNotBlank()

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            title = { Text("¡Producto publicado!") },
            text = { Text("Tu producto ha sido publicado correctamente.") },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }) { Text("Aceptar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vender producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                selectedImageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: run {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Campo de descripción multilineal
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción del producto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                singleLine = false,
                maxLines = 5
            )

            // Categoría con Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    val categories = listOf(
                        "RESISTENCIAS", "CAPACITORES", "CIRCUITOS INTEGRADOS",
                        "TRANSISTORES", "DIODOS", "SENSORES", "DISPLAYS",
                        "FUENTES DE ALIMENTACION", "Otros"
                    )
                    categories.forEach { cat ->
                        DropdownMenuItem(text = { Text(cat) }, onClick = {
                            category = cat
                            expanded = false
                        })
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*$"))) price = it },
                    label = { Text("Precio") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    prefix = { Text("$ ") }
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { if (it.matches(Regex("^\\d*$"))) stock = it },
                    label = { Text("Stock") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            showErrorMessage?.let { msg ->
                Text(text = msg, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    isSubmitting = true
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user == null) {
                        showErrorMessage = "Inicia sesión para publicar"
                        isSubmitting = false
                        return@Button
                    }
                    scope.launch {
                        val imageBytes = selectedImageUri?.let { uri ->
                            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        }
                        val newProduct = Product(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            description = description, // Guarda descripción
                            price = price.toDouble(),
                            stock = stock.toInt(),
                            imageUrl = "",
                            category = category,
                            sellerId = user.uid,
                            sellerName = user.displayName ?: "",
                            timestamp = System.currentTimeMillis(),
                            categoryId = category
                        )
                        val result = productRepository.addProduct(newProduct, imageBytes)
                        if (result.isSuccess) showSuccessDialog = true
                        else showErrorMessage = result.exceptionOrNull()?.message
                        isSubmitting = false
                    }
                },
                enabled = isFormValid && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MosoBlue)
            ) {
                if (isSubmitting) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                else {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Publicar producto")
                }
            }
        }
    }
}



