package com.example.moso.data.model

data class Chat(
    val id: String = "",
    val participantIds: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Map<String, Int> = emptyMap() // userId -> unreadCount
)