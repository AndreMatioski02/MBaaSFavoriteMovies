package com.kotlin.mbaasfirebaseproject.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class User(val uid: String?, val email: String?)

class LoginViewModel constructor() : ViewModel() {

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val authResult = FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(username.value, password.value)
                    .await()
                println("authResult.user ${authResult.user?.uid}")
                _user.value = User(authResult.user?.uid, authResult.user?.email)
                _loginState.value = LoginState.Success
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
