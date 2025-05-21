package com.example.moso.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val photoUrl: String? = null ,
    val productsForSale: List<String> = emptyList(),
    val recentPurchases: List<String> = emptyList()
)




