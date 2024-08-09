package com.kotlin.mbaasfirebaseproject.ui.screens.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel constructor() : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onSignUpClicked() {
        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading

            try {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email.value, password.value)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _signUpState.value = SignUpState.Success
                        } else {
                            _signUpState.value = SignUpState.Error(task.exception?.message ?: "Unknown Error")
                        }
                    }
            } catch (e: Exception) {
                _signUpState.value = SignUpState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}

sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}
