// app/src/main/java/com/example/moso/ui/components/AppDrawerContent.kt
package com.example.moso.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moso.ui.auth.AuthViewModel
import com.example.moso.ui.navigation.Screen


@Composable
fun AppDrawerContent(
    navController: NavController,
    onCloseDrawer: () -> Unit,
    userName: String,
    authViewModel: AuthViewModel // <--- AGREGA ESTO
) {
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
            onCloseDrawer = onCloseDrawer
        )
        DrawerItem(
            label = "Catálogo",
            route = Screen.Catalog.createRoute("all"),
            navController = navController,
            onCloseDrawer = onCloseDrawer
        )
        DrawerItem(
            label = "Compras",
            route = Screen.Purchases.route,
            navController = navController,
            onCloseDrawer = onCloseDrawer
        )
        DrawerItem(
            label = "Ventas",
            route = Screen.Sales.route,
            navController = navController,
            onCloseDrawer = onCloseDrawer
        )

        DrawerItem(
            label = "Mi perfil",
            route = Screen.Profile.route,
            navController = navController,
            onCloseDrawer = onCloseDrawer
        )
        DrawerItem(
            label = "Configuración",
            route = Screen.Settings.route,
            navController = navController,
            onCloseDrawer = onCloseDrawer
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
    onCloseDrawer: () -> Unit
) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(route) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                }
                onCloseDrawer()
            }
            .padding(vertical = 12.dp)
    )
}
