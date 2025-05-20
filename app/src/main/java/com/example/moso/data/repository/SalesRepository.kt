// app/src/main/java/com/example/moso/data/repository/SalesRepository.kt
package com.example.moso.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class SalesRepository {
    private val db = FirebaseFirestore.getInstance()

    /**
     * Cuenta cuántas unidades de tus productos han sido vendidas.
     * Recorre todas las órdenes de todos los usuarios y suma las cantidades
     * de los productos cuyo sellerId coincide con el tuyo.
     */
    suspend fun getTotalSalesForSeller(sellerId: String): Int = coroutineScope {
        // Trae todas las subcolecciones userOrders de todos los usuarios
        val allOrders = db.collectionGroup("userOrders")
            .get()
            .await()

        // Para cada orden, aplanamos lista de pares (productId, quantity)
        val qtyFutures = allOrders.documents.flatMap { doc ->
            @Suppress("UNCHECKED_CAST")
            (doc.get("products") as? List<Map<String, Any>>)?.mapNotNull { item ->
                val pid = item["productId"] as? String ?: return@mapNotNull null
                val qty = (item["quantity"] as? Number)?.toInt() ?: 0
                pid to qty
            } ?: emptyList()
        }.map { (pid, qty) ->
            // Por cada producto, verifica si tú eres el vendedor
            async {
                val p = db.collection("products").document(pid).get().await()
                if (p.getString("sellerId") == sellerId) qty else 0
            }
        }

        // Espera todas y suma
        qtyFutures.map { it.await() }.sum()
    }
}
