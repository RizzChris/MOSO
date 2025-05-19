// data/repository/ChatRepository.kt
package com.example.moso.data.repository

import com.example.moso.data.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repositorio para operaciones de chat en Firestore.
 */
class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null

    /** Crea o recupera un chat entre dos usuarios */
    suspend fun createOrGetChat(userId1: String, userId2: String): Result<String> {
        return try {
            val participants = listOf(userId1, userId2).sorted()
            val existing = db.collection("chats")
                .whereEqualTo("participantIds", participants)
                .get()
                .await()
            val chatId = if (existing.documents.isNotEmpty()) {
                existing.documents.first().id
            } else {
                val newId = db.collection("chats").document().id
                db.collection("chats").document(newId)
                    .set(mapOf(
                        "participantIds" to participants,
                        "lastMessage" to "",
                        "lastMessageTimestamp" to System.currentTimeMillis(),
                        "unreadCount" to mapOf(userId1 to 0, userId2 to 0)
                    )).await()
                newId
            }
            Result.success(chatId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Obtiene los mensajes de un chat ordenados cronológicamente */
    suspend fun getMessagesForChat(chatId: String): Result<List<Message>> {
        return try {
            val snap = db.collection("messages")
                .whereEqualTo("chatId", chatId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .await()
            val msgs = snap.documents.mapNotNull { it.toObject(Message::class.java) }
            Result.success(msgs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Escucha en tiempo real los mensajes nuevos de un chat */
    fun listenForMessages(chatId: String, onNewMessages: (List<Message>) -> Unit) {
        listener?.remove()
        listener = db.collection("messages")
            .whereEqualTo("chatId", chatId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val list = snap.documents.mapNotNull { it.toObject(Message::class.java) }
                onNewMessages(list)
            }
    }

    /** Envía un mensaje a un chat existente */
    suspend fun sendMessage(
        chatId: String,
        senderId: String,
        receiverId: String,
        content: String,
        imageByteArray: ByteArray? = null
    ): Result<Unit> {
        return try {
            val imageUrl = imageByteArray?.let {
                val name = "${UUID.randomUUID()}.jpg"
                val storage = FirebaseStorage.getInstance()
                val ref = storage.reference.child("chat_images/$name")
                ref.putBytes(it).await()
                ref.downloadUrl.await().toString()
            } ?: ""
            val messageId = db.collection("messages").document().id
            val msg = Message(
                id = messageId,
                chatId = chatId,
                senderId = senderId,
                receiverId = receiverId,
                content = content,
                imageUrl = imageUrl,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
            db.collection("messages").document(messageId).set(msg).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}







