package com.example.moso.ui.screens.payment

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun ProcessingScreen(
    navController: NavController,
    onCompletedNavigate: () -> Unit = { navController.navigate("purchases") }
) {
    var completed by remember { mutableStateOf(false) }
    val scaleAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Tiempo de “procesando…”
        delay(2000)
        completed = true
        // Lanza animación de escala
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = TweenSpec(durationMillis = 600)
        )
        // Tiempo para que el usuario lo vea
        delay(1000)
        onCompletedNavigate()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x88000000)), // fondo semi-transparente
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(200.dp)
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (!completed) {
                    // Indicador de carga
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    // Círculo verde con palomita
                    Box(
                        Modifier
                            .size(100.dp)
                            .scale(scaleAnim.value)
                            .background(Color(0xFF4CAF50), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completado",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }
    }
}



