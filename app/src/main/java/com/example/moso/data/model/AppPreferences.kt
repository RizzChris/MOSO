// src/main/java/com/example/moso/data/model/AppPreferences.kt
package com.example.moso.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppPreferences(
    val darkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val language: String = "es",            // es, en, fr...
    val autoPlayCarousel: Boolean = true,   // para el carrusel de Home
    val itemsPerPage: Int = 20,             // paginación
    val supportEmail: String = "soporte@moso.app",
    val privacyPolicyUrl: String = "https://moso.app/privacy",
    val termsOfServiceUrl: String = "https://moso.app/terms",
    val appVersion: String = "1.0.0"
)
