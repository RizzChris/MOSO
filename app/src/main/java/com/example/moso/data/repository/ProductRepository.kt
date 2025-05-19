package com.example.moso.data.repository

import com.example.moso.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProductRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val productsCollection = firestore.collection("products")

    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val querySnapshot = productsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val products = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Product::class.java)
            }

            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsByCategory(category: String): Result<List<Product>> {
        return try {
            val querySnapshot = productsCollection
                .whereEqualTo("category", category)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val products = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Product::class.java)
            }

            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(query: String): Result<List<Product>> {
        return try {
            // Firebase Firestore no soporta búsquedas de texto completo nativamente
            // Esta es una implementación simple que busca coincidencias exactas
            val querySnapshot = productsCollection
                .orderBy("name")
                .get()
                .await()

            val products = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Product::class.java)
            }.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                        product.description.contains(query, ignoreCase = true)
            }

            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(productId: String): Result<Product> {
        return try {
            val documentSnapshot = productsCollection.document(productId).get().await()
            val product = documentSnapshot.toObject(Product::class.java)
                ?: throw Exception("Producto no encontrado")

            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addProduct(product: Product, imageByteArray: ByteArray?): Result<String> {
        return try {
            // Si hay una imagen, subir a Firebase Storage
            val imageUrl = if (imageByteArray != null) {
                val imageName = "${UUID.randomUUID()}.jpg"
                val storageRef = storage.reference.child("product_images/$imageName")
                storageRef.putBytes(imageByteArray).await()
                storageRef.downloadUrl.await().toString()
            } else {
                ""
            }

            // Crear un nuevo documento en Firestore con un ID generado
            val productId = productsCollection.document().id

            // Crear el producto final con la URL de la imagen
            val finalProduct = product.copy(
                id = productId,
                imageUrl = imageUrl
            )

            // Guardar el producto en Firestore
            productsCollection.document(productId).set(finalProduct).await()

            Result.success(productId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            productsCollection.document(product.id).set(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            productsCollection.document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

