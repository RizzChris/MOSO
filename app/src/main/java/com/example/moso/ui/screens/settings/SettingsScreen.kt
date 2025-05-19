package com.example.moso.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moso.ui.theme.QuicksandFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onToggleDarkMode: (Boolean) -> Unit = {},
    onToggleNotifications: (Boolean) -> Unit = {}
) {
    // Local UI state (replace with ViewModel/Datastore as needed)
    var darkModeEnabled by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuración",
                        fontFamily = QuicksandFontFamily
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás"
                        )
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
        ) {
            // Tema oscuro
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Modo oscuro",
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = QuicksandFontFamily,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = darkModeEnabled,
                    onCheckedChange = {
                        darkModeEnabled = it
                        onToggleDarkMode(it)
                    }
                )
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Notificaciones
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notificaciones",
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = QuicksandFontFamily,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        notificationsEnabled = it
                        onToggleNotifications(it)
                    }
                )
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Idioma (placeholder)
            Text(
                text = "Idioma",
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = QuicksandFontFamily,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            // Aquí podrías agregar un DropdownMenu para seleccionar el idioma
        }
    }
}
