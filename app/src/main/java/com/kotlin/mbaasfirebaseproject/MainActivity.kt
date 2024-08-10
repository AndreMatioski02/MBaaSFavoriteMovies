package com.kotlin.mbaasfirebaseproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.mbaasfirebaseproject.navigation.AppNavGraph
import com.kotlin.mbaasfirebaseproject.ui.screens.login.LoginViewModel
import com.kotlin.mbaasfirebaseproject.ui.theme.MBaaSFirebaseProjectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        val currentUser = FirebaseAuth.getInstance().currentUser

        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
            MBaaSFirebaseProjectTheme {
                AppNavGraph(if(currentUser != null) "home" else "login")
            }
        }
    }
}
