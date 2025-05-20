package com.example.moso.ui.screens.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moso.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ChatPreview(
    val chatId: String,
    val userId: String,
    val userName: String,
    val lastMessage: String,
    val lastMessageTimestamp: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController
) {
    // Estado local
    var chats by remember { mutableStateOf<List<ChatPreview>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()

    // Carga las conversaciones al montar la pantalla
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Ajusta "chats" al nombre de tu colección en Firestore
                val snapshot = db.collection("chats").get().await()
                chats = snapshot.documents.mapNotNull { doc ->
                    val participants = doc.get("participants") as? List<String> ?: return@mapNotNull null
                    if (participants.size == 2) {
                        val otherUserId = participants.find { it != FirebaseAuth.getInstance().currentUser?.uid }
                        val userName = doc.getString("userName") ?: return@mapNotNull null
                        val lastMessage = doc.getString("lastMessage") ?: ""
                        val lastMessageTimestamp = doc.getLong("lastMessageTimestamp") ?: 0L
                        ChatPreview(
                            chatId = doc.id,
                            userId = otherUserId ?: return@mapNotNull null,
                            userName = userName,
                            lastMessage = lastMessage,
                            lastMessageTimestamp = lastMessageTimestamp
                        )
                    } else {
                        null
                    }
                }.sortedByDescending { it.lastMessageTimestamp }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chats") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    // Indicador de carga centrado
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    // Mensaje de error
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: $errorMessage",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                else -> {
                    // Lista de chats
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(chats) { chat ->
                            ListItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // Abre el chat 1:1 con este usuario
                                        navController.navigate(
                                            Screen.Chat.createRoute(chat.chatId)
                                        )
                                    },
                                headlineContent = { Text(chat.userName) },
                                supportingContent = { Text(chat.lastMessage) }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}


