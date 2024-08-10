package com.kotlin.mbaasfirebaseproject.ui.screens.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignUpViewModel constructor() : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _cpf = MutableStateFlow("")
    val cpf: StateFlow<String> = _cpf

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onCpfChange(newCpf: String) {
        _cpf.value = newCpf
    }

    fun onSignUpClicked() {
        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading

            try {
                val authResult = FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email.value, password.value)
                    .await()

                val uid = authResult.user?.uid ?: throw Exception("UID is null")

                val userData = hashMapOf(
                    "name" to name.value,
                    "cpf" to cpf.value,
                    "email" to email.value
                )

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(userData)
                    .addOnSuccessListener {
                        _signUpState.value = SignUpState.Success
                    }
                    .addOnFailureListener { exception ->
                        _signUpState.value = SignUpState.Error(exception.message ?: "Unknown Error")
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
