// Archivo: com.example.moso/data/repository/AuthRepository.kt

package com.example.moso.data.repository

import android.net.Uri
import com.example.moso.data.model.User
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage.getInstance
import kotlinx.coroutines.tasks.await
import java.util.UUID



class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    /** Usuario actualmente autenticado en Firebase Auth (o null si no hay ninguno) */
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /** Inicia sesión usando correo/contraseña */
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth
                .signInWithEmailAndPassword(email, password)
                .await()
            Result.success(authResult.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registra un nuevo usuario con correo, contraseña, nombre y apellido.
     * 1) Crea en Auth, 2) actualiza displayName, 3) guarda perfil en Firestore.
     */
    suspend fun register(
        email: String,
        password: String,
        name: String,
        lastName: String
    ): Result<FirebaseUser> {
        return try {
            // 1️⃣ Crear cuenta en Firebase Auth
            val authResult = auth
                .createUserWithEmailAndPassword(email, password)
                .await()
            val user = authResult.user!!

            // 2️⃣ Actualizar perfil de Auth para asignar displayName
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("$name $lastName")
                .build()
            user.updateProfile(profileUpdates).await()

            // 3️⃣ Guardar el documento de perfil en Firestore
            val userProfile = User(
                id       = user.uid,
                name     = name,
                lastName = lastName,
                email    = email,
                photoUrl = null
            )
            usersCollection
                .document(user.uid)
                .set(userProfile)
                .await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Obtiene el perfil de usuario desde Firestore */
    suspend fun getUserProfile(userId: String): Result<User> {
        return try {
            val snapshot = usersCollection
                .document(userId)
                .get()
                .await()
            val user = snapshot.toObject(User::class.java)
                ?: throw Exception("Usuario no encontrado")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Login con Google (idToken obtenido del SDK) */
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            Result.success(authResult.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Login con Facebook (accessToken obtenido del SDK) */
    suspend fun signInWithFacebook(accessToken: String): Result<FirebaseUser> {
        return try {
            val credential = FacebookAuthProvider.getCredential(accessToken)
            val authResult = auth.signInWithCredential(credential).await()
            Result.success(authResult.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfilePhoto(photoUri: Uri): Result<String> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay usuario logueado"))
            // Generar nombre único
            val filename = "avatars/$uid/${UUID.randomUUID()}.jpg"
            val ref = getInstance()
                .getReference(filename)

            // Subir y obtener URL pública
            ref.putFile(photoUri).await()
            val downloadUrl = ref.downloadUrl.await().toString()

            // Actualizar Firestore
            usersCollection.document(uid)
                .update("photoUrl", downloadUrl)
                .await()

            // (Opcional) También actualizar el perfil de Auth si lo usas
            auth.currentUser?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(downloadUrl))
                    .build()
            )?.await()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Cierra sesión */
    fun logout() {
        auth.signOut()
    }
}


