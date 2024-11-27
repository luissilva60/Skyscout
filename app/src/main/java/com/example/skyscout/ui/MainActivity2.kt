package com.example.skyscout.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skyscout.R
import com.example.skyscout.ui.theme.SkyScoutTheme

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkyScoutTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavigationGraph(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val items = listOf(Screen.Home, Screen.Map, Screen.Profile)
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = screen.icon), contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = false,
                onClick = {
                    navController.navigate(screen.route)
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = Screen.Home.route, modifier = modifier) {
        composable(Screen.Home.route) { WeatherHomePage() }
        composable(Screen.Map.route) { MapScreen() }
        composable(Screen.Profile.route) { ProfileScreen() }
    }
}

object Screen {
    val Home = ScreenItem("home", "Home", R.drawable.home)
    val Map = ScreenItem("map", "Map", R.drawable.map)
    val Profile = ScreenItem("profile", "Profile", R.drawable.profile)
}

data class ScreenItem(val route: String, val title: String, val icon: Int)

@Composable
fun WeatherHomePage(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    val cities = listOf(
        CityWeather("Lisbon, Portugal", 25, "Sunny", 60, 15),
        CityWeather("Paris, France", 22, "Cloudy", 70, 10),
        CityWeather("London, UK", 18, "Rainy", 80, 20),
        CityWeather("New York, USA", 28, "Clear", 50, 5)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search City") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal Scroll of Cities
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(cities) { city ->
                CityWeatherCard(city = city)
            }
        }
    }
}

@Composable
fun CityWeatherCard(city: CityWeather) {
    Column(
        modifier = Modifier
            .width(200.dp) // Width of each city card
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Location Name
        Text(text = city.name, fontSize = 18.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))

        // Weather Icon (Placeholder)
        Image(
            painter = painterResource(id = R.drawable.logo), // Replace with weather icon
            contentDescription = "Weather Icon",
            modifier = Modifier.size(50.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Temperature
        Text(text = "${city.temperature}°C", fontSize = 32.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))

        // Weather Condition (e.g., Sunny, Cloudy)
        Text(text = city.condition, fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        // Additional Weather Information
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Humidity", fontSize = 12.sp, color = Color.Black)
                Text(text = "${city.humidity}%", fontSize = 14.sp, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Wind", fontSize = 12.sp, color = Color.Black)
                Text(text = "${city.windSpeed} km/h", fontSize = 14.sp, color = Color.Black)
            }
        }
    }
}

// Data class to represent City Weather
data class CityWeather(
    val name: String,
    val temperature: Int,
    val condition: String,
    val humidity: Int,
    val windSpeed: Int
)

@Composable
fun MapScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Map Screen", fontSize = 24.sp, color = Color.Black)
    }
}

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Profile Screen", fontSize = 24.sp, color = Color.Black)
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherHomePagePreview() {
    SkyScoutTheme {
        WeatherHomePage()
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SkyScoutTheme {
        MainScreen()
    }
}

