// LoginScreen.kt
package com.example.moso.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moso.R
import com.example.moso.ui.auth.AuthViewModel
import com.example.moso.ui.navigation.Screen
import com.example.moso.ui.theme.MOSOTheme
import com.example.moso.ui.theme.MosoBlue
import com.example.moso.ui.theme.MosoBlueDark
import com.example.moso.ui.theme.MosoBlueDeep
import com.example.moso.ui.theme.MosoBrown
import com.example.moso.ui.theme.MosoGray
import com.example.moso.ui.theme.MosoWhite
import com.example.moso.ui.theme.QuicksandFontFamily
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // Nuevo estado para controlar la visibilidad de la contraseña
    var passwordVisible by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val error by viewModel.errorMessage.collectAsState(initial = null)

    // Estados para animaciones
    var showContent by remember { mutableStateOf(false) }
    val logoSize by animateDpAsState(
        targetValue = if (showContent) 180.dp else 220.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoSize"
    )

    // Iniciar animaciones después de que se cargue la pantalla
    LaunchedEffect(key1 = true) {
        delay(100)
        showContent = true
    }

    MOSOTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MosoGray) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo con animación
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "MOSO Logo",
                    modifier = Modifier
                        .size(logoSize)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )

                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(durationMillis = 500)) +
                            slideInVertically(
                                initialOffsetY = { -40 },
                                animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
                            )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "MOSO",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = QuicksandFontFamily,
                            color = MosoBlue
                        )

                        Text(
                            text = "Electronics Store",
                            fontSize = 16.sp,
                            fontFamily = QuicksandFontFamily,
                            color = MosoBrown
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Botones de inicio de sesión social con animación de entrada
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(durationMillis = 600, delayMillis = 300)) +
                            slideInVertically(
                                initialOffsetY = { 40 },
                                animationSpec = tween(durationMillis = 600, delayMillis = 300, easing = FastOutSlowInEasing)
                            )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Botón de Facebook
                        OutlinedButton(
                            onClick = { /* No funcionalidad, solo representativo */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MosoWhite,
                                containerColor = MosoBlue
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                // Icono de Facebook en blanco directamente sobre el fondo azul
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_facebook),
                                    contentDescription = "Facebook",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .align(Alignment.CenterStart)
                                )

                                // Texto centrado
                                Text(
                                    text = "Continuar con Facebook",
                                    color = MosoWhite,
                                    fontSize = 14.sp,
                                    fontFamily = QuicksandFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón de Google
                        OutlinedButton(
                            onClick = { /* No funcionalidad, solo representativo */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MosoBlueDeep,
                                containerColor = MosoWhite
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MosoGray)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                // Ícono a la izquierda sin el box contenedor
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_google),
                                    contentDescription = "Google",
                                    tint = Color.Unspecified,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.CenterStart)
                                )

                                // Texto centrado
                                Text(
                                    text = "Continuar con Google",
                                    color = MosoBlueDeep,
                                    fontSize = 14.sp,
                                    fontFamily = QuicksandFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }

                        // Separador con texto "o"
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Divider(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )
                            Text(
                                text = "o",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Divider(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }

                // Campos de formulario con animación
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(durationMillis = 800, delayMillis = 600)) +
                            slideInVertically(
                                initialOffsetY = { 40 },
                                animationSpec = tween(durationMillis = 800, delayMillis = 600, easing = FastOutSlowInEasing)
                            )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Correo electrónico") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(Modifier.height(8.dp))

                        // Campo de contraseña modificado con icono para mostrar/ocultar
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Contraseña") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                        tint = if (passwordVisible) MosoBlue else Color.Gray
                                    )
                                }
                            }
                        )

                        error?.let {
                            Spacer(Modifier.height(8.dp))
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = {
                                viewModel.login(email.trim(), password)
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MosoBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Iniciar sesión")
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                                Text("Registrarse con correo", color = MosoBlueDark)
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        TextButton(onClick = { /* No funcionalidad */ }) {
                            Text(
                                "Ya tengo una cuenta",
                                fontSize = 14.sp,
                                color = MosoBrown
                            )
                        }
                    }
                }
            }
        }
    }
}