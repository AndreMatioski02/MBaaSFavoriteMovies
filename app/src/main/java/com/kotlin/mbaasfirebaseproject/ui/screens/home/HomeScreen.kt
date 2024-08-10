package com.kotlin.mbaasfirebaseproject.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.mbaasfirebaseproject.ui.screens.login.LoginViewModel

@Composable
fun HomeScreen(loginViewModel: LoginViewModel = viewModel(), onLogoutButtonClicked: () -> Unit) {
    val user by loginViewModel.user.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                loginViewModel.clearUserState()
                onLogoutButtonClicked()
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
                .padding(16.dp)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(user != null) {
                Text("Seja bem-vindo!", color = Color(0xFF0A00CC), style = TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight(500)))

                Text("ID: ${user?.uid}", color = Color(0xFF0A00CC), style = TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight(500)))

                Text("Nome: ${user?.name}", color = Color(0xFF0A00CC), style = TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight(500)))

                Text("CPF: ${user?.cpf}", color = Color(0xFF0A00CC), style = TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight(500)))

                Text("Email: ${user?.email}", color = Color(0xFF0A00CC), style = TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight(500)))
            } else {
                CircularProgressIndicator()
            }
            }
    }
}

