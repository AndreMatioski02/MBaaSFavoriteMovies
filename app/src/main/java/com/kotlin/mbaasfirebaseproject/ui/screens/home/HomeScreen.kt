package com.kotlin.mbaasfirebaseproject.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.mbaasfirebaseproject.ui.screens.login.LoginViewModel

@Composable
fun HomeScreen(navController: NavController, loginViewModel: LoginViewModel = hiltViewModel()) {
    val userUID = FirebaseAuth.getInstance().currentUser?.uid

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Logout",
                tint = Color.Black,
                modifier = Modifier.size(48.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Seja bem-vindo!", color = Color(0xFF0A00CC), style = TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight(500)))
            Spacer(modifier = Modifier.height(16.dp))
            Text("ID: $userUID", color = Color(0xFF0A00CC), style = TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight(500)))

        }
    }
}

