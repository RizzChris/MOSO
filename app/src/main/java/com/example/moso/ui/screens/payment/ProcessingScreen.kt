package com.example.moso.ui.screens.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moso.ui.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun ProcessingScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Procesando pago...")
        }
    }

    // Simula el proceso de pago y redirige
    LaunchedEffect(Unit) {
        delay(3000)  // 3 segundos de simulación
        navController.navigate(Screen.Purchases.route) {
            popUpTo(Screen.Processing.route) { inclusive = true }
        }
    }
}


