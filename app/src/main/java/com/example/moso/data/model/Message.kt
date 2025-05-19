// data/model/Message.kt
package com.example.moso.data.model

/**
 * Representa un mensaje dentro de un chat.
 */
data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false
)
