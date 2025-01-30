package com.example.skyscout.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.skyscout.ui.viewmodels.WeatherViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchedCityWeather by viewModel.searchedCityWeather.collectAsState()

    Scaffold(
        topBar = {
            // TopAppBar with a Back button
            TopAppBar(
                title = { Text("Search City") },
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
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    if (query.isNotEmpty()) {
                        viewModel.fetchWeatherForCity(query)
                    } else {
                        viewModel.clearSearchResult()
                    }
                },
                label = { Text("Search City") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors()
            )

            // Display City Weather Full Screen or Blank Placeholder
            if (searchedCityWeather != null) {
                CityWeatherFullScreen(
                    cityWeather = searchedCityWeather!!,
                    currentIndex = 0,
                    totalCities = 1
                )
            } else if (searchQuery.isNotEmpty() && searchedCityWeather == null) {
                Text(
                    text = "No results found for \"$searchQuery\".",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
