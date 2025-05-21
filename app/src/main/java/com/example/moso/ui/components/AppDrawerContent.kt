// app/src/main/java/com/example/moso/ui/components/AppDrawerContent.kt
package com.example.moso.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moso.ui.auth.AuthViewModel
import com.example.moso.ui.navigation.Screen

// Define el color mosoablue
private val MosoBlue = Color(0xFF2196F3) // Puedes cambiar este valor por tu color específico

@Composable
fun AppDrawerContent(
    navController: NavController,
    onCloseDrawer: () -> Unit,
    userName: String,
    authViewModel: AuthViewModel
) {
    // Estado para manejar qué item está seleccionado
    var selectedItem by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Saludo al usuario
        Text(
            text = "¡Hola, $userName!",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 24.dp)
        )

        // Opciones de navegación
        DrawerItem(
            label = "Inicio",
            route = Screen.Home.route,
            navController = navController,
            onCloseDrawer = onCloseDrawer,
            isSelected = selectedItem == "Inicio",
            onItemClick = { selectedItem = "Inicio" }
        )
        DrawerItem(
            label = "Catálogo",
            route = Screen.Catalog.createRoute("all"),
            navController = navController,
            onCloseDrawer = onCloseDrawer,
            isSelected = selectedItem == "Catálogo",
            onItemClick = { selectedItem = "Catálogo" }
        )
        DrawerItem(
            label = "Compras",
            route = Screen.Purchases.route,
            navController = navController,
            onCloseDrawer = onCloseDrawer,
            isSelected = selectedItem == "Compras",
            onItemClick = { selectedItem = "Compras" }
        )
        DrawerItem(
            label = "Ventas",
            route = Screen.Sales.route,
            navController = navController,
            onCloseDrawer = onCloseDrawer,
            isSelected = selectedItem == "Ventas",
            onItemClick = { selectedItem = "Ventas" }
        )
        DrawerItem(
            label = "Carrito",
            route = Screen.Cart.route,
            navController = navController,
            onCloseDrawer = onCloseDrawer,
            isSelected = selectedItem == "Carrito",
            onItemClick = { selectedItem = "Carrito" }
        )
        DrawerItem(
            label = "Mi perfil",
            route = Screen.Profile.route,
            navController = navController,
            onCloseDrawer = onCloseDrawer,
            isSelected = selectedItem == "Mi perfil",
            onItemClick = { selectedItem = "Mi perfil" }
        )
        DrawerItem(
            label = "Configuración",
            route = Screen.Settings.route,
            navController = navController,
            onCloseDrawer = onCloseDrawer,
            isSelected = selectedItem == "Configuración",
            onItemClick = { selectedItem = "Configuración" }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Cerrar sesión
        Text(
            text = "Cerrar sesión",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    authViewModel.logout()
                    onCloseDrawer()
                }
                .padding(vertical = 12.dp)
        )
    }
}

@Composable
private fun DrawerItem(
    label: String,
    route: String,
    navController: NavController,
    onCloseDrawer: () -> Unit,
    isSelected: Boolean,
    onItemClick: () -> Unit
) {
    // Animación del color de fondo
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MosoBlue.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "background_color_animation"
    )

    // Animación del color del texto
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MosoBlue else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(durationMillis = 300),
        label = "text_color_animation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable {
                onItemClick()
                navController.navigate(route) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                }
                onCloseDrawer()
            }
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )
    }
}