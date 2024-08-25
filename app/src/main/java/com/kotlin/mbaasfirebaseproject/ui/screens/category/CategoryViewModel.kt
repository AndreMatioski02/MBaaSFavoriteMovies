package com.kotlin.mbaasfirebaseproject.ui.screens.category

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CategoryViewModel : ViewModel() {
    private val _categories = mutableStateOf<List<Category>>(emptyList())
    val categories: State<List<Category>> = _categories
    private val db = FirebaseFirestore.getInstance()

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> get() = _successMessage

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _getCategoryState = MutableStateFlow<GetCategoryState>(GetCategoryState.Idle)
    val getCategoryState: StateFlow<GetCategoryState> = _getCategoryState
    fun addCategory(userId: String, categoryName: String) {
        val newCategory = hashMapOf(
            "name" to categoryName
        )

        db.collection("users").document(userId).collection("categories")
            .add(newCategory)
            .addOnSuccessListener {
                fetchCategories(userId)
                _successMessage.value = "Categoria criada com sucesso!"
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Erro ao criar categoria: ${e.message}"
            }
    }

    fun fetchCategories(userId: String) {
        _categories.value = emptyList()
        _getCategoryState.value = GetCategoryState.Loading

        db.collection("users").document(userId)
            .collection("categories")
            .get()
            .addOnSuccessListener { result ->
                val categoryList = result.documents.map { document ->
                    Category(
                        id = document.id,
                        name = document.getString("name") ?: "Sem Nome"
                    )
                }

                _categories.value = categoryList

                _getCategoryState.value = GetCategoryState.Success
            }
            .addOnFailureListener { exception ->
                _getCategoryState.value = GetCategoryState.Error(exception.message ?: "Unknown Error")
            }
    }

    fun updateCategoryName(userId: String, categoryId: String, newName: String) {
        val categoryRef = db.collection("users").document(userId).collection("categories").document(categoryId)
        categoryRef.update("name", newName)
            .addOnSuccessListener {
                fetchCategories(userId)
                _successMessage.value = "Categoria atualizada com sucesso!"
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Erro ao atualizar categoria: ${e.message}"
            }
    }

    fun deleteCategory(userId: String, categoryId: String) {
        val categoryRef = db.collection("users").document(userId).collection("categories").document(categoryId)
        categoryRef.delete()
            .addOnSuccessListener {
                fetchCategories(userId)
                _successMessage.value = "Categoria excluída com sucesso!"
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Erro ao excluir categoria: ${e.message}"
            }
    }

    fun resetMessages() {
        _successMessage.value = null
        _errorMessage.value = null
    }
}

sealed class GetCategoryState {
    object Idle : GetCategoryState()
    object Loading : GetCategoryState()
    object Success : GetCategoryState()
    data class Error(val message: String) : GetCategoryState()
}

data class Category(
    val id: String,
    val name: String
)
