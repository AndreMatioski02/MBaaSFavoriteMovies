package com.kotlin.mbaasfirebaseproject.ui.screens.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.navigation.NavController
import com.kotlin.mbaasfirebaseproject.ui.components.CustomTextField

@Composable
fun SignUpScreen(navController: NavController, signUpViewModel: SignUpViewModel = viewModel()) {
    val name by signUpViewModel.name.collectAsState()
    val cpf by signUpViewModel.cpf.collectAsState()
    val email by signUpViewModel.email.collectAsState()
    val password by signUpViewModel.password.collectAsState()
    val signupState by signUpViewModel.signUpState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFF0A00CC)),
                    startY = 0.0f,
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = {
                navController.navigate("login")
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = Color(0xFF0A00CC),
                modifier = Modifier.size(48.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Crie sua conta", style = TextStyle(fontSize = 32.sp, color = Color(0xFF0A00CC)), textAlign = TextAlign.Center, fontWeight = FontWeight(500))

            CustomTextField("Nome", name, { signUpViewModel.onNameChange(it) }, false)

            CustomTextField("CPF", cpf, { signUpViewModel.onCpfChange(it) }, false)

            CustomTextField("Email", email, { signUpViewModel.onEmailChange(it) }, false)

            CustomTextField("Senha", password, { signUpViewModel.onPasswordChange(it) }, true)

            Button(onClick = { signUpViewModel.onSignUpClicked() }, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A00CC)), shape = RoundedCornerShape(100.dp), contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text("Cadastrar", style = TextStyle(fontSize = 24.sp, color = Color(0xFFFFFFFF)))
            }

            when (signupState) {
                is SignUpState.Idle -> {
                    // Do nothing
                }
                is SignUpState.Loading -> {
                    CircularProgressIndicator()
                }
                is SignUpState.Success -> {
                    navController.navigate("home")
                }
                is SignUpState.Error -> {
                    Text("Usuário já existente ou senha muito curta. Tente novamente!", color = Color(0xFFCC0014), style = TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight(400)))
                    Text("Error: ${(signupState as SignUpState.Error).message}")
                }
            }
        }
    }
}
