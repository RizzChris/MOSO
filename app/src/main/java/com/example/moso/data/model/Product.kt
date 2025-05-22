package com.example.moso.data.model

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val imageUrl: String = "",
    val category: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val categoryId: String = "",      // <-- default ""
    val imageResId: Int = 0           // <-- default 0
)

// Categorías de productos
object ProductCategories {
    const val RESISTORS          = "RESISTENCIAS"
    const val CAPACITORS         = "CAPACITORES"
    const val INTEGRATED_CIRCUITS= "CIRCUITOS INTEGRADOS"
    const val TRANSISTORS        = "TRANSISTORES"
    const val DIODES             = "DIODOS"
    const val SENSORS            = "SENSORES"
    const val DISPLAYS           = "DISPLAYS"
    const val CONNECTORS         = "CONECTORES"            // ← cambia aquí
    const val POWER_SUPPLIES     = "FUENTES DE ALIMENTACION"
    const val OTHER              = "OTROS"                 // ← y aquí

    val allCategories = listOf(
        RESISTORS, CAPACITORS, INTEGRATED_CIRCUITS, TRANSISTORS,
        DIODES, SENSORS, DISPLAYS, CONNECTORS, POWER_SUPPLIES, OTHER
    )
}

