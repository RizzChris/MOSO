package com.example.moso.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 🎨 Colores personalizados para MOSO
private val LightColors = lightColorScheme(
    primary = MosoBlue,            // Azul principal
    secondary = MosoBrown,         // Marrón
    background = MosoGray,         // Fondo gris claro
    surface = MosoWhite,           // Superficie blanca
    onPrimary = MosoWhite,
    onSecondary = MosoWhite,
    onBackground = MosoBlueDark,   // Texto sobre fondo claro
    onSurface = MosoBlueDark,
    error = ErrorRed,
    tertiary = AccentOrange
)

private val DarkColors = darkColorScheme(
    primary = MosoBlue,
    secondary = MosoBrown,
    background = MosoBlueDeep,
    surface = MosoGray,
    onPrimary = MosoWhite,
    onSecondary = MosoWhite,
    onBackground = MosoWhite,
    onSurface = MosoBlueDark,
    error = ErrorRed,
    tertiary = AccentOrange
)

@Composable
fun MOSOTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    // Cambia el color de la barra de estado
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

