package com.kotlin.mbaasfirebaseproject.ui.screens.movie

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MovieViewModel: ViewModel() {
    private val _movies = mutableStateOf<List<Movie>>(emptyList())
    val movies: State<List<Movie>> = _movies
    private val db = FirebaseFirestore.getInstance()

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> get() = _successMessage

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _getMovieState = MutableStateFlow<GetMovieState>(GetMovieState.Idle)
    val getMovieState: StateFlow<GetMovieState> = _getMovieState

    fun addMovie(userId: String, movie: AddMovieClass) {
        val newMovie = hashMapOf(
            "movieName" to movie.movieName,
            "rate" to 0,
            "platformToWatch" to movie.platformToWatch,
            "watched" to 0,
            "categoryId" to movie.categoryId,
        )
        db.collection("users").document(userId).collection("categories").document(movie.categoryId).collection("movies")
            .add(newMovie)
            .addOnSuccessListener {
                fetchMovies(userId, movie.categoryId)
                _successMessage.value = "Filme criado com sucesso!"
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Erro ao criar filme: ${e.message}"
            }
    }

    fun fetchMovies(userId: String, categoryId: String) {
        _movies.value = emptyList()
        _getMovieState.value = GetMovieState.Loading

        db.collection("users").document(userId)
            .collection("categories").document(categoryId)
            .collection("movies")
            .get()
            .addOnSuccessListener { result ->
                val movieList = result.documents.map { document ->
                    Movie(
                        movieId = document.id,
                        movieName = document.getString("movieName") ?: "Sem Nome",
                        rate = document.getLong("rate")?.toInt() ?: 0,
                        platformToWatch = document.getString("platformToWatch") ?: "Sem plataforma",
                        watched = document.getLong("watched")?.toInt() ?: 0,
                        categoryId = document.getString("categoryId") ?: "Sem categoria",
                    )
                }

                _movies.value = movieList

                _getMovieState.value = GetMovieState.Success
            }
            .addOnFailureListener { exception ->
                _getMovieState.value = GetMovieState.Error(exception.message ?: "Unknown Error")
            }
    }

    fun updateMovie(userId: String, movie: Movie) {
        val movieRef = db.collection("users").document(userId).collection("categories").document(movie.categoryId).collection("movies").document(movie.movieId)
        movieRef.update("movieName", movie.movieName, "rate", movie.rate, "platformToWatch", movie.platformToWatch, "watched", movie.watched)
            .addOnSuccessListener {
                fetchMovies(userId, movie.categoryId)
                _successMessage.value = "Filme atualizada com sucesso!"
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Erro ao atualizar filme: ${e.message}"
            }
    }
    fun deleteMovie(userId: String, movie: Movie) {
        val movieRef = db.collection("users").document(userId).collection("categories").document(movie.categoryId).collection("movies").document(movie.movieId)
        movieRef.delete()
            .addOnSuccessListener {
                fetchMovies(userId, movie.categoryId)
                _successMessage.value = "Filme excluído com sucesso!"
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Erro ao excluir filme: ${e.message}"
            }
    }


    fun resetMessages() {
        _successMessage.value = null
        _errorMessage.value = null
    }
}

sealed class GetMovieState {
    object Idle : GetMovieState()
    object Loading : GetMovieState()
    object Success : GetMovieState()
    data class Error(val message: String) : GetMovieState()
}

data class Movie(
    var movieId: String,
    var movieName: String,
    var rate: Int?,
    var platformToWatch: String,
    var watched: Int,
    var categoryId: String
)

data class AddMovieClass(
    var movieName: String,
    var platformToWatch: String,
    var categoryId: String
)