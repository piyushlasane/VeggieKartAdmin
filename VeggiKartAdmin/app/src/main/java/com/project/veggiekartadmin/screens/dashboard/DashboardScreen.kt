package com.project.veggiekartadmin.screens.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.project.veggiekartadmin.navigation.Routes
import com.project.veggiekartadmin.screens.banners.BannersScreen
import com.project.veggiekartadmin.screens.categories.CategoriesScreen
import com.project.veggiekartadmin.screens.products.ProductsScreen

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController) {
    val navItems = listOf(
        BottomNavItem("Products", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag),
        BottomNavItem("Categories", Icons.Filled.Category, Icons.Outlined.Category),
        BottomNavItem("Banners", Icons.Filled.Image, Icons.Outlined.Image),
    )

    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val topBarTitle = when (selectedIndex) {
        0 -> "Products"
        1 -> "Categories"
        2 -> "Banners"
        else -> ""
    }

    val fabRoute = when (selectedIndex) {
        0 -> Routes.addEditProduct()
        1 -> Routes.addEditCategory()
        else -> null
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle, fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            fabRoute?.let { route ->
                FloatingActionButton(
                    onClick = { navController.navigate(route) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        },
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
                navController = navController,
                snackbarHostState = snackbarHostState
            )
            1 -> CategoriesScreen(
                modifier = Modifier.padding(paddingValues),
                navController = navController,
                snackbarHostState = snackbarHostState
            )
            2 -> BannersScreen(
                modifier = Modifier.padding(paddingValues),
                navController = navController,
                snackbarHostState = snackbarHostState
            )
        }
    }
}