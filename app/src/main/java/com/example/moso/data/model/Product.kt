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
    const val RESISTORS = "Resistencias"
    const val CAPACITORS = "Capacitores"
    const val INTEGRATED_CIRCUITS = "Circuitos Integrados"
    const val TRANSISTORS = "Transistores"
    const val DIODES = "Diodos"
    const val SENSORS = "Sensores"
    const val DISPLAYS = "Displays"
    const val CONNECTORS = "Conectores"
    const val POWER_SUPPLIES = "Fuentes de Alimentación"
    const val OTHER = "Otros"

    val allCategories = listOf(
        RESISTORS,
        CAPACITORS,
        INTEGRATED_CIRCUITS,
        TRANSISTORS,
        DIODES,
        SENSORS,
        DISPLAYS,
        CONNECTORS,
        POWER_SUPPLIES,
        OTHER
    )
}

