// ui/navigation/Screen.kt
package com.example.moso.ui.navigation

sealed class Screen(val route: String) {
    object Login         : Screen("login")
    object Register      : Screen("register")
    object Home          : Screen("home")
    object Catalog       : Screen("catalog")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object SellProduct   : Screen("sell_product")
    object Cart          : Screen("cart")
    object ChatList      : Screen("chat_list")
    object Chat          : Screen("chat/{userId}") {
        fun createRoute(userId: String) = "chat/$userId"
    }
    object Search        : Screen("search")
    object Profile       : Screen("profile")
    object Settings      : Screen("settings")
}




