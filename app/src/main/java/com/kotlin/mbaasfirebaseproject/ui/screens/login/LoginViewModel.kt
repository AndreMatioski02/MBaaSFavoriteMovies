package com.kotlin.mbaasfirebaseproject.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class User(val uid: String?, val email: String?, val name: String?, val cpf: String?)

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

    suspend fun getUserState(uid: String) {
        val userDocument = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .await()

        val name = userDocument.getString("name")
        val cpf = userDocument.getString("cpf")
        val email = userDocument.getString("email")

        _user.value = User(uid, email, name, cpf)
        _loginState.value = LoginState.Success
    }
    fun clearUserState() {
        _user.value = null
        _username.value = ""
        _password.value = ""
        _loginState.value = LoginState.Idle
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val authResult = FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(username.value, password.value)
                    .await()

                val uid = authResult.user?.uid ?: throw Exception("UID is null")

                getUserState(uid)
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
