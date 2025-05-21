// RegisterScreen.kt
package com.example.moso.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moso.R
import com.example.moso.ui.auth.AuthViewModel
import com.example.moso.ui.navigation.Screen
import com.example.moso.ui.theme.MOSOTheme
import com.example.moso.ui.theme.QuicksandFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Nuevos estados de error
    var nombreError by remember { mutableStateOf<String?>(null) }
    var apellidoError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val serverError by viewModel.errorMessage.collectAsState(initial = null)

    MOSOTheme {
        Surface(modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(40.dp))
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "MOSO Logo",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text("MOSO",
                    style = MaterialTheme.typography.displayLarge,
                    fontFamily = QuicksandFontFamily)
                Text("Crear cuenta",
                    style = MaterialTheme.typography.displayMedium,
                    fontFamily = QuicksandFontFamily)
                Spacer(Modifier.height(24.dp))

                // Nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        nombreError = null
                    },
                    label = { Text("Nombre") },
                    isError = nombreError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                nombreError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 16.dp, top = 4.dp))
                }
                Spacer(Modifier.height(8.dp))

                // Apellido
                OutlinedTextField(
                    value = apellido,
                    onValueChange = {
                        apellido = it
                        apellidoError = null
                    },
                    label = { Text("Apellido") },
                    isError = apellidoError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                apellidoError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 16.dp, top = 4.dp))
                }
                Spacer(Modifier.height(8.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    label = { Text("Correo electrónico") },
                    isError = emailError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                emailError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 16.dp, top = 4.dp))
                }
                Spacer(Modifier.height(8.dp))

                // Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    label = { Text("Contraseña") },
                    isError = passwordError != null,
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                passwordError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 16.dp, top = 4.dp))
                }
                Spacer(Modifier.height(8.dp))

                // Confirmar contraseña
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = null
                    },
                    label = { Text("Confirmar contraseña") },
                    isError = confirmPasswordError != null,
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                confirmPasswordError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 16.dp, top = 4.dp))
                }

                // Error del servidor
                serverError?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        // Validaciones
                        nombreError = if (nombre.isBlank()) "El nombre es obligatorio" else null
                        apellidoError = if (apellido.isBlank()) "El apellido es obligatorio" else null
                        emailError = when {
                            email.isBlank() -> "El correo es obligatorio"
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                                "Formato de correo inválido"
                            else -> null
                        }
                        passwordError = when {
                            password.isBlank() -> "La contraseña es obligatoria"
                            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
                            else -> null
                        }
                        confirmPasswordError = when {
                            confirmPassword.isBlank() -> "Debes confirmar tu contraseña"
                            confirmPassword != password -> "Las contraseñas no coinciden"
                            else -> null
                        }

                        // Si no hay errores, procedemos
                        if (listOf(
                                nombreError,
                                apellidoError,
                                emailError,
                                passwordError,
                                confirmPasswordError
                            ).all { it == null }
                        ) {
                            viewModel.register(email.trim(), password, nombre, apellido)
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Registrarse")
                    }
                }

                Spacer(Modifier.height(12.dp))

                TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                    Text("Ya tengo cuenta")
                }
            }
        }
    }
}

