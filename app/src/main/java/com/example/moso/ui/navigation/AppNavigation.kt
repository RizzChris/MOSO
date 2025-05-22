package com.example.moso.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moso.ui.auth.AuthViewModel
import com.example.moso.ui.components.AppDrawerContent
import com.example.moso.ui.screens.auth.LoginScreen
import com.example.moso.ui.screens.auth.RegisterScreen
import com.example.moso.ui.screens.cart.CartScreen
import com.example.moso.ui.screens.catalog.CatalogScreen
import com.example.moso.ui.screens.chat.ChatListScreen
import com.example.moso.ui.screens.chat.ChatScreen
import com.example.moso.ui.screens.home.HomeScreen
import com.example.moso.ui.screens.order.OrderDetailScreen
import com.example.moso.ui.screens.payment.ProcessingScreen
import com.example.moso.ui.screens.posts.PostsScreen
import com.example.moso.ui.screens.product.EditProductScreen
import com.example.moso.ui.screens.product.ProductDetailScreen
import com.example.moso.ui.screens.product.SellProductScreen
import com.example.moso.ui.screens.profile.ProfileScreen
import com.example.moso.ui.screens.purchases.PurchasesScreen
import com.example.moso.ui.screens.sales.SalesScreen
import com.example.moso.ui.screens.search.SearchScreen
import com.example.moso.ui.screens.settings.SettingsScreen
import com.example.moso.ui.theme.MosoBlue
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val userName by authViewModel.currentUserName.collectAsState(initial = null)

    if (userName == null) {
        // — Login / Register —
        NavHost(navController, startDestination = Screen.Login.route) {
            composable(Screen.Login.route)    { LoginScreen(navController, authViewModel) }
            composable(Screen.Register.route) { RegisterScreen(navController, authViewModel) }
        }
        return
    }

    // — Usuario logueado: drawer + bottom nav + navhost —
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(280.dp)
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                AppDrawerContent(
                    navController = navController,
                    onCloseDrawer = { scope.launch { drawerState.close() } },
                    userName      = userName!!,
                    authViewModel = authViewModel
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MosoBlue),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    title = { Text("MOSO", color = Color.White) },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(Screen.Search.route)
                        }) {
                            Icon(Icons.Default.Search, tint = Color.White, contentDescription = "Buscar")
                        }
                    }
                )
            },
            bottomBar = {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                NavigationBar(
                    containerColor = MosoBlue,    // Fondo azul MOSO
                    contentColor   = Color.White  // Íconos y texto en blanco
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio") },
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            selectedTextColor   = Color.White,
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                        label = { Text("Perfil") },
                        selected = currentRoute == Screen.Profile.route,
                        onClick = { navController.navigate(Screen.Profile.route) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            selectedTextColor   = Color.White,
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Compras") },
                        label = { Text("Compras") },
                        selected = currentRoute == Screen.Purchases.route,
                        onClick = { navController.navigate(Screen.Purchases.route) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            selectedTextColor   = Color.White,
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.ChatBubble, contentDescription = "Chat") },
                        label = { Text("Chat") },
                        selected = currentRoute == Screen.ChatList.route,
                        onClick = { navController.navigate(Screen.ChatList.route) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            selectedTextColor   = Color.White,
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        ) { inner ->
            Box(Modifier.padding(inner)) {
                NavHost(navController, startDestination = Screen.Home.route) {

                    composable(Screen.Catalog.route) { back ->
                        val catId = back.arguments?.getString("categoryId")
                        CatalogScreen(navController, if (catId == "all") null else catId)
                    }


                    composable(Screen.ProductDetail.route) { backStackEntry ->
                        val pid = backStackEntry.arguments?.getString("productId") ?: return@composable
                        ProductDetailScreen(pid, navController)
                    }
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onNavigateToSell = { navController.navigate(Screen.SellProduct.route) },
                            onNavigateToCatalog = { categoryId ->
                                navController.navigate("catalog/$categoryId")
                            },
                            onNavigateToProductDetail = { pid ->
                                navController.navigate(Screen.ProductDetail.createRoute(pid))
                            },
                            onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                            onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                            onNavigateToChat = { navController.navigate(Screen.ChatList.route) }
                        )
                    }
                    composable(Screen.SellProduct.route)  { SellProductScreen(navController) }
                    composable(Screen.Cart.route)         { CartScreen(navController) }
                    composable(Screen.ChatList.route) {
                        ChatListScreen(navController)
                    }
                    composable(Screen.Search.route) {
                        SearchScreen(navController)
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen(
                            navController = navController,
                            onNavigateUp = { navController.navigateUp() }
                        )
                    }

                    composable(Screen.Purchases.route) { PurchasesScreen(navController) }
                    // Ruta para las compras
                    composable(Screen.Settings.route){ SettingsScreen(navController) }

                    composable(
                        route = Screen.OrderDetail.route,
                        arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val orderId = backStackEntry.arguments!!.getString("orderId")!!
                        OrderDetailScreen(orderId, navController)
                    }

                    // Ruta para las compras
                    composable(Screen.Sales.route) {
                        SalesScreen(navController)
                    }
                    // nueva ruta para editar
                    composable(
                        route = Screen.EditProduct.route,
                        arguments = listOf(navArgument("productId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val productId = backStackEntry.arguments!!.getString("productId")!!
                        EditProductScreen(productId = productId, navController = navController)
                    }


                    composable(
                        route = Screen.Chat.route,
                        arguments = listOf(navArgument("userId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        ChatScreen(backStackEntry.arguments!!.getString("userId")!!, navController)
                    }




                    composable(
                        route = Screen.Chat.route,
                        arguments = listOf(navArgument("userId") {
                            type = NavType.StringType
                        })
                    ) { backStack ->
                        val userId = backStack.arguments?.getString("userId")?: return@composable
                        ChatScreen(userId, navController)
                    }
                    composable(Screen.Processing.route) {
                        ProcessingScreen(navController)
                    }
                    composable(Screen.Posts.route) {
                        PostsScreen(navController)
                    }

                }
            }
        }
    }
}

