package com.example.skyscout.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyscout.MainActivity
import com.example.skyscout.data.models.FavoritePlace
import com.example.skyscout.data.models.User
import com.example.skyscout.data.models.UserSession
import com.example.skyscout.network.DatabaseRetrofitInstance
import com.example.skyscout.network.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DBViewModel : ViewModel() {

    // State flows for managing UI state
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val _loginResponse = MutableStateFlow<LoginResponse?>(null)
    val loginResponse = _loginResponse.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _favoriteCities = MutableStateFlow<List<FavoritePlace>>(emptyList())
    val favoriteCities = _favoriteCities.asStateFlow()

    private val _favorites: MutableState<List<FavoritePlace>> = mutableStateOf(emptyList())
    val favorites: MutableState<List<FavoritePlace>> = _favorites


    // Fetch all users
    fun fetchUsers() {
        viewModelScope.launch {
            try {
                val response = DatabaseRetrofitInstance.instance.getUsers()
                if (response.isSuccessful) {
                    _users.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error fetching users: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Exception occurred: ${e.message}"
            }
        }
    }

    // Login user
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = DatabaseRetrofitInstance.instance.login(
                    mapOf("userEmail" to email, "userPassword" to password)
                )
                if (response.isSuccessful) {
                    _loginResponse.value = response.body()
                } else {
                    _errorMessage.value = "Error logging in: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Exception occurred: ${e.message}"
            }
        }
    }

    // Fetch favorite places for a given user
    fun fetchFavorites(userId: String) {
        viewModelScope.launch {
            try {
                val response = DatabaseRetrofitInstance.instance.getFavorites(userId)
                if (response.isSuccessful) {
                    _favoriteCities.value = response.body()?.favorites ?: emptyList()
                } else {
                    _errorMessage.value = "Error fetching favorite cities: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Exception occurred: ${e.message}"
            }
        }
    }
    // Add favorite place
    fun addFavorite(placeName: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = DatabaseRetrofitInstance.instance.addFavorite(
                    mapOf("placeName" to placeName, "placeUserId" to userId)
                )
                if (!response.isSuccessful) {
                    _errorMessage.value = "Error adding favorite: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Exception occurred: ${e.message}"
            }
        }
    }
    fun deleteFavorite(favoriteId: String) {
        viewModelScope.launch {
            try {
                // Log before making the API call
                Log.d("DBViewModel", "Deleting favorite with ID: $favoriteId")

                // Call the deleteFavorite API endpoint, passing the favoriteId as a query parameter
                val response = DatabaseRetrofitInstance.instance.deleteFavorite(favoriteId)

                if (response.isSuccessful) {
                    Log.d("DBViewModel", "Favorite deleted successfully")

                    // After successful deletion, update both _favoriteCities and _favorites
                    _favoriteCities.value = _favoriteCities.value.filter { it._id != favoriteId }
                    _favorites.value = _favorites.value.filter { it._id != favoriteId }

                } else {
                    Log.e("DBViewModel", "Error deleting favorite: ${response.message()}")
                    _errorMessage.value = "Error deleting favorite: ${response.message()}"
                }
            } catch (e: Exception) {
                Log.e("DBViewModel", "Exception occurred: ${e.message}")
                _errorMessage.value = "Exception occurred: ${e.message}"
            }
        }
    }

    fun createUser(userEmail: String, userPassword: String) {
        viewModelScope.launch {
            try {
                // Prepare the request body
                val body = mapOf("userEmail" to userEmail, "userPassword" to userPassword)

                // Call the API endpoint to create a new user
                val response = DatabaseRetrofitInstance.instance.createUser(body)

                if (response.isSuccessful) {
                    // Handle success (e.g., show a success message, navigate to login, etc.)
                } else {
                    _errorMessage.value = "Error creating user: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Exception occurred: ${e.message}"
            }
        }
    }






    fun onLogoutClick(context: Context) {
        // Clear the session
        UserSession.currentUser = null

        // Navigate to MainActivity (Login screen)
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

}
