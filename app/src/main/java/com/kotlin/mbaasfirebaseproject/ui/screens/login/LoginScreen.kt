package com.kotlin.mbaasfirebaseproject.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotlin.mbaasfirebaseproject.ui.components.CustomTextField

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel(), onLoginSuccess: () -> Unit, onSignupClicked: () -> Unit) {
    val username by loginViewModel.username.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val loginState by loginViewModel.loginState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A00CC), Color(0xFFFFFFFF)),
                    startY = 0.0f,
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Bem-vindo ao MyFavoriteMovie!", style = TextStyle(fontSize = 32.sp, color = Color(0xFFFFFFFF)), textAlign = TextAlign.Center, fontWeight = FontWeight(500))
            Spacer(modifier = Modifier.height(32.dp))
            CustomTextField("Email", username, { loginViewModel.onUsernameChange(it) }, false)
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField("Senha", password, { loginViewModel.onPasswordChange(it) }, true)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { loginViewModel.onLoginClicked() }, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A00CC)), shape = RoundedCornerShape(100.dp), contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text("Acessar", style = TextStyle(fontSize = 24.sp, color = Color(0XFFFFFFFF)))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onSignupClicked() }, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), shape = RoundedCornerShape(100.dp), contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text("Não possui login? Cadastre-se", style = TextStyle(fontSize = 24.sp, color = Color(0xFF0A00CC)), textAlign = TextAlign.Center, fontWeight = FontWeight(500))
            }
            Spacer(modifier = Modifier.height(16.dp))

            when (loginState) {
                is LoginState.Idle -> {
                    // Do nothing
                }
                is LoginState.Loading -> {
                    CircularProgressIndicator()
                }
                is LoginState.Success -> {
                    onLoginSuccess()
                }
                is LoginState.Error -> {
                    Text("Usuário ou senha incorreto. Tente novamente!", color = Color(0xFFCC0014), style = TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight(400)))
                    // Text("Error: ${(loginState as LoginState.Error).message}")
                }
            }
        }
    }
}
