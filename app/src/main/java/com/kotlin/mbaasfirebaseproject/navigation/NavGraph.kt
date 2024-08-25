package com.kotlin.mbaasfirebaseproject.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kotlin.mbaasfirebaseproject.ui.screens.category.CategoryScreen
import com.kotlin.mbaasfirebaseproject.ui.screens.category.CategoryViewModel
import com.kotlin.mbaasfirebaseproject.ui.screens.login.LoginScreen
import com.kotlin.mbaasfirebaseproject.ui.screens.login.LoginViewModel
import com.kotlin.mbaasfirebaseproject.ui.screens.signup.SignUpScreen

@Composable
fun AppNavGraph(startDestination: String = "login") {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(loginViewModel, onLoginSuccess = {
                navController.navigate("categories")
            }, onSignupClicked = { navController.navigate("signup")})
        }
        composable("categories") {
            CategoryScreen(loginViewModel, categoryViewModel) {
                navController.navigate("login") {
                    popUpTo("category") { inclusive = true }
                }
            }
        }
        composable("signup") {
            SignUpScreen(navController)
        }
    }
}

