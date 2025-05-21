// ui/screens/chat/ChatListScreen.kt
package com.example.moso.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
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
    val lastMessageTimestamp: Long,
    val userAvatarUrl: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController
) {
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
                val myId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val snapshot = db.collection("chats")
                    .whereArrayContains("participants", myId)
                    .get()
                    .await()

                chats = snapshot.documents.mapNotNull { doc ->
                    // 1) Recuperar al otro participante
                    val participants = doc.get("participants") as? List<String> ?: return@mapNotNull null
                    val otherUserId = participants.firstOrNull { it != myId }
                        ?: return@mapNotNull null

                    // 2) Obtener nombre completo del otro usuario
                    val userDoc = db.collection("users")
                        .document(otherUserId)
                        .get()
                        .await()
                    val name     = userDoc.getString("name")     ?: "Usuario"
                    val lastName = userDoc.getString("lastName") ?: ""
                    val userName = "$name $lastName".trim()
                    val avatarUrl  = userDoc.getString("photoUrl")

                    // 3) Obtener último mensaje del array `messages`
                    val msgs = doc.get("messages") as? List<Map<String,Any>> ?: emptyList()
                    val last = msgs.lastOrNull()
                    val text = last?.get("content")   as? String ?: ""
                    val time = last?.get("timestamp") as? Long   ?: 0L

                    ChatPreview(
                        chatId               = doc.id,
                        userId               = otherUserId,
                        userName          = "$name $lastName".trim(),
                        userAvatarUrl     = avatarUrl,
                        lastMessage          = text,
                        lastMessageTimestamp = time
                    )
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
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
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
                    if (chats.isEmpty()) {
                        Text(
                            "No tienes conversaciones aún.",
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        itemsIndexed(chats) { index, chat ->
                            val background = if (index % 2 == 0) Color(0xFFF0F0F0) else Color.White
                            ListItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(background, RoundedCornerShape(8.dp))
                                    .clickable { navController.navigate(Screen.Chat.createRoute(chat.userId)) }
                                    .padding(vertical = 8.dp),
                                leadingContent = {
                                    AsyncImage(
                                        model = chat.userAvatarUrl
                                            ?: "https://avatar.iran.liara.run/public", // o URL de fallback
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                        contentScale = ContentScale.Crop
                                    )
                                },
                                headlineContent = { Text(chat.userName) },
                                supportingContent = { Text(chat.lastMessage, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            )
                        }
                    }

                }
            }
        }
    }
}



