package com.example.moso.data.repository

import android.net.Uri
import com.example.moso.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

/**
 * Repositorio para operaciones CRUD de productos,
 * incluyendo subida de imagen a Firebase Storage.
 */
class ProductRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val productsCollection = firestore.collection("products")
    private val storageRef = FirebaseStorage.getInstance().reference

    /**
     * Añade o actualiza un producto. Si `imageUri` no es nulo, sube primero la imagen y
     * actualiza el campo `imageUrl` con la URL pública.
     */
    suspend fun addProduct(product: Product, imageUri: Uri?): Result<Unit> {
        return try {
            // Si hay URI de imagen, súbela y obtén URL
            val imageUrl = imageUri?.let { uri ->
                // Incluye extensión .jpg para consistencia
                val path = "product_images/${product.id}.jpg"
                val ref = storageRef.child(path)
                // Sube archivo y espera
                ref.putFile(uri).await()
                // Obtén URL pública
                ref.downloadUrl.await().toString()
            } ?: product.imageUrl

            // Copia modelo con URL definitiva
            val prodWithUrl = product.copy(imageUrl = imageUrl)

            // Guarda en Firestore (crea o actualiza)
            productsCollection.document(prodWithUrl.id)
                .set(prodWithUrl)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene todos los productos, ordenados por fecha de creación descendente.
     */
    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val snap = productsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            val list = snap.documents
                .mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Filtra productos por categoríaId.
     */
    suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> {
        return try {
            val snap = productsCollection
                .whereEqualTo("categoryId", categoryId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            val list = snap.documents
                .mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Busca productos cuyo nombre o descripción contengan el texto (sin subidas).
     */
    suspend fun searchProducts(query: String): Result<List<Product>> {
        return try {
            val snap = productsCollection
                .orderBy("name")
                .get()
                .await()
            val filtered = snap.documents
                .mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) }
                .filter { p ->
                    p.name.contains(query, ignoreCase = true)
                            || p.description.contains(query, ignoreCase = true)
                }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene un producto por su ID.
     */
    suspend fun getProductById(id: String): Result<Product> {
        return try {
            val doc = productsCollection.document(id).get().await()
            val product = doc.toObject(Product::class.java)?.copy(id = id)
                ?: throw Exception("Producto no encontrado")
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza un producto existente (sin manejar imagen).
     */
    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            productsCollection.document(product.id).set(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsBySeller(sellerId: String): Result<List<Product>> {
        return try {
            val snap = firestore.collection("products")
                .whereEqualTo("sellerId", sellerId)
                .get().await()
            val list = snap.documents.mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) }
            Result.success(list)
        } catch(e:Exception) {
            Result.failure(e)
        }
    }


    /**
     * Elimina un producto (y su imagen en Storage).
     */
    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            // Borra documento
            productsCollection.document(productId).delete().await()
            // Borra imagen asociada
            val imgRef = storageRef.child("product_images/$productId.jpg")
            imgRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


