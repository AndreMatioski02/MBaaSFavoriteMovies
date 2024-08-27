package com.kotlin.mbaasfirebaseproject.ui.screens.category

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.mbaasfirebaseproject.ui.screens.login.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(loginViewModel: LoginViewModel = viewModel(), categoryViewModel: CategoryViewModel, navController: NavController ,onLogoutButtonClicked: () -> Unit) {
    val user by loginViewModel.user.collectAsState()
    val categories by categoryViewModel.categories

    val getCategoryState by categoryViewModel.getCategoryState.collectAsState()

    val context = LocalContext.current
    val successMessage by categoryViewModel.successMessage.observeAsState()
    val errorMessage by categoryViewModel.errorMessage.observeAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Category?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Category?>(null) }

    successMessage?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        categoryViewModel.resetMessages()
    }

    errorMessage?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        categoryViewModel.resetMessages()
    }
    LaunchedEffect(user) {
        user?.uid?.let { categoryViewModel.fetchCategories(it) }
    }


    val currentUser = FirebaseAuth.getInstance().currentUser

    if (currentUser != null) {
        LaunchedEffect(currentUser.uid) {
            loginViewModel.getUserState(currentUser.uid)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        loginViewModel.clearUserState()
                        onLogoutButtonClicked()
                    }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                    }
                },
                actions = {
                    if(user != null) {
                        Text(text = "${user?.name}")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Filled.Person, contentDescription = "Person")
                    } else {
                        CircularProgressIndicator()
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = { showAddDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A00CC))) {
                Text("Adicionar Categoria")
            }


            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(vertical = 25.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Categorias",
                            style = MaterialTheme.typography.headlineLarge
                        )

                        when (getCategoryState) {
                            is GetCategoryState.Idle -> {
                                // Do nothing
                            }
                            is GetCategoryState.Loading -> {
                                CircularProgressIndicator()
                            }
                            is GetCategoryState.Success -> {
                                if(categories.isEmpty()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Não há categorias cadastradas.",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                            is GetCategoryState.Error -> {
                                Text("Erro ao buscar categorias", color = Color(0xFFCC0014), style = TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight(400)))
                                // Text("Error: ${(signupState as GetCategoryState.Error).message}")
                            }
                        }

                    }
                }
                if(categories.isNotEmpty()) {
                    items(categories) { category ->
                        CategoryItem(
                            category = category,
                            onEditClick = { showEditDialog = category },
                            onDeleteClick = { showDeleteDialog = category },
                            onCategoryClick = {
                                navController.navigate("movie/${category.id}")
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCategoryDialog(
            onDismissRequest = { showAddDialog = false },
            onConfirmClick = { categoryName ->
                currentUser?.uid?.let { categoryViewModel.addCategory(it, categoryName) }
                showAddDialog = false
            }
        )
    }

    showEditDialog?.let { category ->
        EditCategoryDialog(
            category = category,
            onDismissRequest = { showEditDialog = null },
            onConfirmClick = { newName ->
                currentUser?.uid?.let { categoryViewModel.updateCategoryName(it, category.id, newName) }
                showEditDialog = null
            }
        )
    }

    showDeleteDialog?.let { category ->
        DeleteCategoryDialog(
            onDismissRequest = { showDeleteDialog = null },
            onConfirmClick = {
                currentUser?.uid?.let { categoryViewModel.deleteCategory(it, category.id) }
                showDeleteDialog = null
            }
        )
    }
}

@Composable
fun CategoryItem(
    category: Category,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCategoryClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A00CC), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
            .clickable { onCategoryClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        Row {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Excluir", tint = Color.White)
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun AddCategoryDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: (String) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Adicionar Nova Categoria") },
        text = {
            Column {
                TextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Nome da Categoria") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        onConfirmClick(categoryName)
                    }
                }
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
fun EditCategoryDialog(
    category: Category,
    onDismissRequest: () -> Unit,
    onConfirmClick: (String) -> Unit
) {
    var newName by remember { mutableStateOf(category.name) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Editar Categoria") },
        text = {
            Column {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nome da Categoria") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirmClick(newName) }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DeleteCategoryDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Excluir Categoria") },
        text = { Text("Tem certeza que deseja excluir esta categoria?") },
        confirmButton = {
            Button(onClick = onConfirmClick) {
                Text("Excluir")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}



