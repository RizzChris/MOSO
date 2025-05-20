package com.example.moso.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moso.data.model.Message
import com.example.moso.data.model.User
import com.example.moso.ui.theme.MosoBlue
import com.example.moso.ui.theme.QuicksandFontFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    userId: String,
    navController: NavController
) {
    val me = FirebaseAuth.getInstance().currentUser
    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()

    var chatId by remember { mutableStateOf<String?>(null) }
    var messages by remember { mutableStateOf(emptyList<Message>()) }
    var messageText by remember { mutableStateOf("") }
    var recipientUser by remember { mutableStateOf<User?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // 1) Crear o recuperar chatId
    LaunchedEffect(userId, me?.uid) {
        me?.uid?.let { myId ->
            val chatRef = firestore.collection("chats")
            val chatDoc = chatRef.whereArrayContains("participants", myId).whereArrayContains("participants", userId).get().await()

            if (chatDoc.isEmpty) {
                // Si no existe el chat, crearlo
                val newChatRef = chatRef.add(mapOf(
                    "participants" to listOf(myId, userId),
                    "messages" to emptyList<Message>()
                )).await()

                chatId = newChatRef.id
            } else {
                // Si ya existe el chat
                chatId = chatDoc.documents[0].id
            }
        }
    }

    // 2) Cargar y escuchar mensajes
    LaunchedEffect(chatId) {
        chatId?.let { id ->
            isLoading = true
            val chatRef = firestore.collection("chats").document(id)
            chatRef.get().addOnSuccessListener { snapshot ->
                val loadedMessages = snapshot.get("messages") as List<Map<String, Any>>
                messages = loadedMessages.map { map ->
                    Message(
                        content = map["content"] as String,
                        senderId = map["senderId"] as String,
                        timestamp = map["timestamp"] as Long
                    )
                }
            }
            isLoading = false
            chatRef.addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    val updatedMessages = it.get("messages") as List<Map<String, Any>>
                    messages = updatedMessages.map { map ->
                        Message(
                            content = map["content"] as String,
                            senderId = map["senderId"] as String,
                            timestamp = map["timestamp"] as Long
                        )
                    }
                    scope.launch {
                        if (messages.isNotEmpty())
                            listState.scrollToItem(messages.size - 1)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = recipientUser?.photoUrl
                                .takeIf { !it.isNullOrEmpty() }
                                ?: "https://via.placeholder.com/40",
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = listOfNotNull(recipientUser?.name, recipientUser?.lastName)
                                .joinToString(" ")
                                .ifBlank { "Usuario" },
                            fontFamily = QuicksandFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escribe un mensaje...") }
                    )
                    FloatingActionButton(onClick = {
                        val myId = me?.uid ?: return@FloatingActionButton
                        val cid = chatId ?: return@FloatingActionButton
                        val newMessage = Message(
                            content = messageText,
                            senderId = myId,
                            timestamp = System.currentTimeMillis()
                        )
                        firestore.collection("chats").document(cid)
                            .update("messages", FieldValue.arrayUnion(newMessage))
                        messageText = ""
                    }, containerColor = MosoBlue) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center), color = MosoBlue)
                errorMessage != null -> Text(errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                messages.isEmpty() -> Text("No hay mensajes.", modifier = Modifier.align(Alignment.Center))
                else -> LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(messages) { msg ->
                        val isMine = msg.senderId == me?.uid
                        MessageBubble(msg, isFromCurrentUser = isMine)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, isFromCurrentUser: Boolean) {
    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start
    ) {
        Box(
            Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isFromCurrentUser) MosoBlue else MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp)
        ) {
            Text(message.content, color = if (isFromCurrentUser)
                MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(time, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
    }
}
