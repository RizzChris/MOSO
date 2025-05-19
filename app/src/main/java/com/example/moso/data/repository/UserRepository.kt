package com.example.moso.data.repository

import com.example.moso.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    /** Obtiene un usuario por su ID de documento en Firestore */
    suspend fun getUserById(userId: String): Result<User> {
        return try {
            val snapshot = usersCollection
                .document(userId)
                .get()
                .await()
            // Copiamos snapshot.id en el campo `id`
            val user = snapshot.toObject(User::class.java)
                ?.copy(id = snapshot.id)
                ?: throw Exception("Usuario no encontrado")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


