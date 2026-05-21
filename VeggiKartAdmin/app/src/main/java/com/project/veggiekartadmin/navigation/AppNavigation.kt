package com.project.veggiekartadmin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.project.veggiekartadmin.screens.auth.LoginScreen
import com.project.veggiekartadmin.screens.products.AddEditProductScreen
import com.project.veggiekartadmin.screens.categories.AddEditCategoryScreen
import com.project.veggiekartadmin.screens.banners.BannersScreen
import com.project.veggiekartadmin.screens.dashboard.DashboardScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null)
        Routes.DASHBOARD else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(navController = navController)
        }
        composable(Routes.ADD_EDIT_PRODUCT) {
            val productId = it.arguments?.getString("productId")
            val actualId = if (productId == "null") null else productId
            AddEditProductScreen(navController = navController, productId = actualId)
        }
        composable(Routes.ADD_EDIT_CATEGORY) {
            val categoryId = it.arguments?.getString("categoryId")
            val actualId = if (categoryId == "null") null else categoryId
            AddEditCategoryScreen(navController = navController, categoryId = actualId)
        }
        composable(Routes.BANNERS) {
            BannersScreen(navController = navController)
        }
    }
}