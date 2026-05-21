package com.project.veggiekartadmin.screens.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.project.veggiekartadmin.screens.banners.BannersScreen
import com.project.veggiekartadmin.screens.categories.CategoriesScreen
import com.project.veggiekartadmin.screens.products.ProductsScreen

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun DashboardScreen(navController: NavHostController) {
    val navItems = listOf(
        BottomNavItem("Products", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag),
        BottomNavItem("Categories", Icons.Filled.Category, Icons.Outlined.Category),
        BottomNavItem("Banners", Icons.Filled.Image, Icons.Outlined.Image),
    )

    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        label = { Text(item.label) },
                        icon = {
                            Icon(
                                imageVector = if (selectedIndex == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedIndex) {
            0 -> ProductsScreen(
                modifier = Modifier.padding(paddingValues),
                navController = navController
            )
            1 -> CategoriesScreen(
                modifier = Modifier.padding(paddingValues),
                navController = navController
            )
            2 -> BannersScreen(
                modifier = Modifier.padding(paddingValues),
                navController = navController
            )
        }
    }
}