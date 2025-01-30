package com.example.skyscout.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skyscout.ui.viewmodel.DBViewModel
import com.example.skyscout.data.models.UserSession
import com.example.skyscout.data.models.FavoritePlace


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(navController: NavController, viewModel: DBViewModel) {
    // Get the current user from the session
    val user = UserSession.currentUser
    var favoriteCities by remember { mutableStateOf(listOf<FavoritePlace>()) }
    var newCity by remember { mutableStateOf(TextFieldValue("")) }

    // Fetch favorite cities when the screen is first launched
    LaunchedEffect(user?._id) {
        user?._id?.let {
            // Fetch favorite cities for the current user
            viewModel.fetchFavorites(it.toString())
        }
    }

    // Observe changes to the favorite cities from the ViewModel
    val usersFavoriteCities by viewModel.favoriteCities.collectAsState()

    // Update favoriteCities when the view model provides data
    LaunchedEffect(usersFavoriteCities) {
        favoriteCities = usersFavoriteCities
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Favorite Cities") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Favorite Cities", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

            // Display the current favorite cities in a list
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(favoriteCities.size) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Display city name as non-editable text
                        Text(
                            text = favoriteCities[index].placeName, // Use the placeName from FavoritePlace
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )

                        // Delete button for each city
                        IconButton(
                            onClick = {
                                val cityToDelete = favoriteCities[index]
                                // Call ViewModel to delete the favorite city
                                user?._id?.let {
                                    viewModel.deleteFavorite(cityToDelete._id) // Pass the city ID and user ID
                                }
                                Toast.makeText(navController.context, "City Removed!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete City"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Option to add a new favorite city
            OutlinedTextField(
                value = newCity,
                onValueChange = { newCity = it },
                label = { Text("Add New City") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                singleLine = true
            )

            Button(
                onClick = {
                    // Add the new city to the favorite list
                    if (newCity.text.isNotEmpty()) {
                        user?._id?.let {
                            viewModel.addFavorite(newCity.text, it.toString()) // Add city to favorites
                        }
                        newCity = TextFieldValue("") // Clear the input field
                        Toast.makeText(navController.context, "City Added!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add City")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
