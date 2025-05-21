package com.example.moso.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moso.data.model.AppPreferences
import com.example.moso.data.repository.PreferencesRepository
import com.example.moso.ui.navigation.Screen
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
    fun SettingsScreen(
        navController: NavController,
        prefsRepo: PreferencesRepository = PreferencesRepository(LocalContext.current)
    ) {
        val prefs by prefsRepo.prefsFlow.collectAsState(initial = AppPreferences())
        val coroutineScope = rememberCoroutineScope()
        Scaffold(
            /* … tu TopAppBar … */
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                // Oscuro
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Modo oscuro", Modifier.weight(1f))
                    Switch(
                        checked = prefs.darkMode,
                        onCheckedChange = { coroutineScope.launch { prefsRepo.updateAutoPlay(it) } }
                    )
                }
                Divider(Modifier.padding(vertical = 8.dp))

                // Notificaciones
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Notificaciones", Modifier.weight(1f))
                    Switch(
                        checked = prefs.notificationsEnabled,
                        onCheckedChange = { coroutineScope.launch { prefsRepo.updateAutoPlay(it) } }
                    )
                }
                Divider(Modifier.padding(vertical = 8.dp))

                // Autoplay carousel
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Reproducir carrusel automáticamente", Modifier.weight(1f))
                    Switch(
                        checked = prefs.autoPlayCarousel,
                        onCheckedChange = { coroutineScope.launch { prefsRepo.updateAutoPlay(it) } }
                    )
                }
                Divider(Modifier.padding(vertical = 8.dp))

                // Items por página (ejemplo con botones)
                Text("Items por página: ${prefs.itemsPerPage}", Modifier.padding(vertical = 8.dp))
                Row {
                    TextButton(onClick = { coroutineScope.launch { prefsRepo.updateItemsPerPage(prefs.itemsPerPage - 1) } }) { Text("-") }
                    TextButton(onClick = { coroutineScope.launch { prefsRepo.updateItemsPerPage(prefs.itemsPerPage + 1) } }) { Text("+") }
                }
                Divider(Modifier.padding(vertical = 8.dp))

                // Versión, política, soporte…
                Text(
                    text = "Versión de la app: ${prefs.appVersion}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Screen.Profile.route) }
                        .padding(vertical = 8.dp)
                )
                TextButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                    Text("Política de privacidad")
                }
                TextButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                    Text("Términos de servicio")
                }
                Text(
                    text = "Soporte: ${prefs.supportEmail}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Screen.Profile.route) }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }


