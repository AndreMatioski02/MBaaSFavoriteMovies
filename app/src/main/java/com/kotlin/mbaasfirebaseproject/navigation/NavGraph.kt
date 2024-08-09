package com.kotlin.mbaasfirebaseproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kotlin.mbaasfirebaseproject.ui.screens.home.HomeScreen
import com.kotlin.mbaasfirebaseproject.ui.screens.login.LoginScreen
import com.kotlin.mbaasfirebaseproject.ui.screens.signup.SignUpScreen

@Composable
fun AppNavGraph(startDestination: String = "login") {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController)
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("signup") {
            SignUpScreen(navController)
        }
    }
}

