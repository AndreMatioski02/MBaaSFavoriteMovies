package com.kotlin.mbaasfirebaseproject.ui.screens.movie

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.mbaasfirebaseproject.ui.screens.login.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(loginViewModel: LoginViewModel = viewModel(), movieViewModel: MovieViewModel = viewModel(), categoryUid: String?, onArrowBackClick: () -> Unit) {
    val user by loginViewModel.user.collectAsState()
    val movies by movieViewModel.movies

    val getMovieState by movieViewModel.getMovieState.collectAsState()

    val context = LocalContext.current
    val successMessage by movieViewModel.successMessage.observeAsState()
    val errorMessage by movieViewModel.errorMessage.observeAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Movie?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Movie?>(null) }

    successMessage?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        movieViewModel.resetMessages()
    }

    errorMessage?.let { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        movieViewModel.resetMessages()
    }
    LaunchedEffect(categoryUid) {
        user?.uid?.let {
            if (categoryUid != null) {
                movieViewModel.fetchMovies(it,categoryUid)
            }
        }
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
                    IconButton(onClick = { onArrowBackClick() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
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
                Text("Adicionar Filme")
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
                            "Filmes",
                            style = MaterialTheme.typography.headlineLarge
                        )

                        when (getMovieState) {
                            is GetMovieState.Idle -> {
                                // Do nothing
                            }
                            is GetMovieState.Loading -> {
                                CircularProgressIndicator()
                            }
                            is GetMovieState.Success -> {
                                if(movies.isEmpty()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Não há filmes cadastrados.",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                            is GetMovieState.Error -> {
                                Text("Erro ao buscar filmes", color = Color(0xFFCC0014), style = TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight(400)))
                            }
                        }

                    }
                }
                if(movies.isNotEmpty()) {
                    items(movies) { movie ->
                        MovieItem(
                            movie = movie,
                            onEditClick = { showEditDialog = movie },
                            onDeleteClick = { showDeleteDialog = movie },
                            onCheckBoxClick  = { updatedMovie -> currentUser?.uid?.let { movieViewModel.updateMovie(it, updatedMovie) } },
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        if (categoryUid != null) {
            AddMovieDialog(
                categoryUid,
                onDismissRequest = { showAddDialog = false },
                onConfirmClick = { movie ->
                    currentUser?.uid?.let { movieViewModel.addMovie(it, movie) }
                    showAddDialog = false
                }
            )
        }
    }

    showEditDialog?.let { movie ->
        EditMovieDialog(
            movie,
            onDismissRequest = { showEditDialog = null },
            onConfirmClick = { newMovie ->
                currentUser?.uid?.let { movieViewModel.updateMovie(it, newMovie) }
                showEditDialog = null
            }
        )
    }

    showDeleteDialog?.let { movie ->
        DeleteMovieDialog(
            onDismissRequest = { showDeleteDialog = null },
            onConfirmClick = {
                currentUser?.uid?.let {
                    if (categoryUid != null) {
                        movieViewModel.deleteMovie(it, movie)
                    }
                }
                showDeleteDialog = null
            }
        )
    }
}

@Composable
fun MovieItem(
    movie: Movie,
    onEditClick: () -> Unit,
    onCheckBoxClick: (movie: Movie) -> Unit,
    onDeleteClick: () -> Unit,
) {
    val rateText = if (movie.rate == 0) {
        "Sem Avaliação"
    } else {
        movie.rate.toString()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .border(4.dp, Color(0xFF0A00CC), shape = RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Checkbox(
                    checked = movie.watched == 1,
                    onCheckedChange = {
                        val updatedMovie = movie.copy(watched = if (movie.watched == 1) 0 else 1)
                        onCheckBoxClick(updatedMovie)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF0A00CC),
                        uncheckedColor = Color(0xFF0A00CC),
                        checkmarkColor = Color.White
                    )
                )
                Text(
                        text = "Assistido?",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.movieName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Plataforma: ${movie.platformToWatch}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nota: $rateText",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color(0xFF0A00CC)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir",
                    tint = Color(0xFF0A00CC)
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}





@Composable
fun AddMovieDialog(
    categoryUid: String,
    onDismissRequest: () -> Unit,
    onConfirmClick: (AddMovieClass) -> Unit
) {
    var movieNameCaptured by remember { mutableStateOf("") }
    var platformToWatchCaptured by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Adicionar novo filme") },
        text = {
            Column {
                TextField(
                    value = movieNameCaptured,
                    onValueChange = { movieNameCaptured = it },
                    label = { Text("Nome do filme:") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = platformToWatchCaptured,
                    onValueChange = { platformToWatchCaptured = it },
                    label = { Text("Disponível na plataforma:") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (movieNameCaptured.isNotBlank() && platformToWatchCaptured.isNotBlank()) {
                        val movie = AddMovieClass(
                                movieName = movieNameCaptured,
                                platformToWatch = platformToWatchCaptured,
                                categoryId = categoryUid
                                )
                        onConfirmClick(movie)
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
fun EditMovieDialog(
    movie: Movie,
    onDismissRequest: () -> Unit,
    onConfirmClick: (Movie) -> Unit
) {
    var movieNameCaptured by remember { mutableStateOf(movie.movieName) }
    var rateCaptured by remember { mutableStateOf(if(movie.rate == 0) {""} else {movie.rate.toString()}) }
    var platformToWatchCaptured by remember { mutableStateOf(movie.platformToWatch) }
    var watchedWatchCaptured by remember { mutableStateOf(movie.watched) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Adicionar novo filme") },
        text = {
            Column {
                TextField(
                    value = movieNameCaptured,
                    onValueChange = { movieNameCaptured = it },
                    label = { Text("Nome do filme:") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = platformToWatchCaptured,
                    onValueChange = { platformToWatchCaptured = it },
                    label = { Text("Disponível na plataforma:") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = rateCaptured,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() } && it.toIntOrNull() in 1..5) {
                            rateCaptured = it
                        }
                    },
                    label = { Text("Avaliação (1-5):") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "1 - Ruim, 2 - Regular, 3 - Bom, 4 - Muito Bom, 5 - Excelente",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = watchedWatchCaptured == 1,
                        onCheckedChange = { watchedWatchCaptured = if(it) {1} else {0} }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Já assistido")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (movieNameCaptured.isNotBlank() && platformToWatchCaptured.isNotBlank() && rateCaptured.isNotBlank()) {
                        val newMovie = Movie(
                            movieId = movie.movieId,
                            movieName = movieNameCaptured,
                            platformToWatch = platformToWatchCaptured,
                            rate = rateCaptured.toInt(),
                            watched = watchedWatchCaptured,
                            categoryId = movie.categoryId
                        )
                        onConfirmClick(newMovie)
                    }
                }
            ) {
                Text("Alterar")
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
fun DeleteMovieDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Excluir filme") },
        text = { Text("Tem certeza que deseja excluir este filme?") },
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