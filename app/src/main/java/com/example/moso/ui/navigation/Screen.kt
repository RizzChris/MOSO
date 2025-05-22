package com.example.moso.ui.navigation

sealed class Screen(val route: String) {
    object Login         : Screen("login")
    object Register      : Screen("register")
    object Home          : Screen("home")
    object Catalog : Screen("catalog/{categoryId}") {
        fun createRoute(categoryId: String) = "catalog/$categoryId"
    }
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object SellProduct   : Screen("sell_product")
    object Cart          : Screen("cart")
    object ChatList : Screen("chats")
    // ui/navigation/Screen.kt
    object Chat : Screen("chat/{userId}") {
        fun createRoute(userId: String) = "chat/$userId"
    }
    object Search        : Screen("search")
    object Profile       : Screen("profile")
    object Settings      : Screen("settings")
    object Purchases     : Screen("purchases")  // Ruta para las compras
    object OrderDetail   : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }
    object Sales      : Screen("sales")
    object Processing : Screen("processing")
    object EditProduct : Screen("edit_product/{productId}") {
        fun createRoute(productId: String) = "edit_product/$productId"
    }

    object Posts       : Screen("posts")
}









