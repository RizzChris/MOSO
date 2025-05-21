package com.example.moso.ui.screens.auth

import android.app.Activity
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moso.R
import com.example.moso.ui.auth.AuthViewModel
import com.example.moso.ui.navigation.Screen
import com.example.moso.ui.theme.MOSOTheme
import com.example.moso.ui.theme.MosoBlue
import com.example.moso.ui.theme.MosoBlueDeep
import com.example.moso.ui.theme.MosoBrown
import com.example.moso.ui.theme.MosoGray
import com.example.moso.ui.theme.QuicksandFontFamily
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    // Estados
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val serverError by viewModel.errorMessage.collectAsState(initial = null)

    // Contexto para Google y Facebook
    val context = LocalContext.current
    val activity = context as Activity

    // — Google Sign-In setup —
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { viewModel.loginWithGoogle(it) }
            } catch (e: ApiException) {
                viewModel.setErrorMessage("Error al autenticar con Google")
            }
        }
    }

    MOSOTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MosoGray) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Espacio superior
                    Spacer(modifier = Modifier.height(50.dp))

                    // Logo
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "MOSO Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 8.dp),
                        contentScale = ContentScale.Fit
                    )

                    // Título
                    Text(
                        text = "MOSO",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = QuicksandFontFamily,
                        color = MosoBlue
                    )

                    Text(
                        text = "Electronics Store",
                        fontSize = 14.sp,
                        fontFamily = QuicksandFontFamily,
                        color = MosoBrown
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón de Facebook
                    Button(
                        onClick = {
                            LoginManager.getInstance().logInWithReadPermissions(
                                activity,
                                listOf("email", "public_profile")
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF073763) // Azul más oscuro como en la imagen
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.icon_facebook),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Continuar con Facebook", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón de Google - MODIFICADO: eliminado el contorno
                    Button(
                        onClick = { googleLauncher.launch(googleClient.signInIntent) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.DarkGray
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.icon_google),
                                contentDescription = "Logo Google",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Continuar con Google",
                                color = MosoBlueDeep,
                                fontSize = 14.sp,
                                fontFamily = QuicksandFontFamily,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Separador con texto "o"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            color = Color.LightGray
                        )
                        Text(
                            text = "o",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Divider(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            color = Color.LightGray
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de correo electrónico
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError != null) emailError = null
                        },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        isError = emailError != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = MosoBlue
                        )
                    )
                    emailError?.let { msg ->
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 16.dp, top = 4.dp),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Campo de contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (passwordError != null) passwordError = null
                        },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        isError = passwordError != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = MosoBlue
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible }
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible)
                                        "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = if (passwordVisible) MosoBlue else Color.Gray
                                )
                            }
                        }
                    )
                    passwordError?.let { msg ->
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 16.dp, top = 4.dp),
                            fontSize = 12.sp
                        )
                    }

                    // Error del servidor
                    serverError?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de iniciar sesión
                    Button(
                        onClick = {
                            // Validaciones locales
                            emailError = when {
                                email.isBlank() -> "El correo es obligatorio"
                                !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                                    "Formato de correo inválido"

                                else -> null
                            }
                            passwordError = when {
                                password.isBlank() -> "La contraseña es obligatoria"
                                password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
                                else -> null
                            }
                            // Si todo bien, llamar a ViewModel
                            if (emailError == null && passwordError == null) {
                                viewModel.login(email.trim(), password)
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF073763)), // Azul oscuro como en la imagen
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Enlaces de registro y recuperación
                    TextButton(
                        onClick = { navController.navigate(Screen.Register.route) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Crear una cuenta nueva",
                            color = MosoBlue,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // MODIFICADO: Centrado la "o" entre las opciones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "o",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    TextButton(
                        onClick = { /* Funcionalidad de recuperación de cuenta */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Ya tengo una cuenta",
                            color = MosoBrown,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}