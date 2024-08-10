package com.kotlin.mbaasfirebaseproject.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kotlin.mbaasfirebaseproject.ui.screens.home.HomeScreen
import com.kotlin.mbaasfirebaseproject.ui.screens.login.LoginScreen
import com.kotlin.mbaasfirebaseproject.ui.screens.login.LoginViewModel
import com.kotlin.mbaasfirebaseproject.ui.screens.signup.SignUpScreen

@Composable
fun AppNavGraph(startDestination: String = "login") {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(loginViewModel, onLoginSuccess = {
                navController.navigate("home")
            }, onSignupClicked = { navController.navigate("signup")})
        }
        composable("home") {
            HomeScreen(loginViewModel) {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }
        composable("signup") {
            SignUpScreen(navController)
        }
    }
}

