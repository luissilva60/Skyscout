package com.example.skyscout.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.skyscout.R
import com.example.skyscout.data.models.CityWeather
import com.example.skyscout.data.models.ForecastResponse
import com.example.skyscout.data.models.WeatherResponse
import com.example.skyscout.ui.viewmodels.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherHomePage(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf("") }
    val citiesWeather by viewModel.citiesWeather.collectAsState()
    val searchedCityWeather by viewModel.searchedCityWeather.collectAsState()

    val context = LocalContext.current

    // Call the fetch function when this Composable is launched
    LaunchedEffect(Unit) {
        viewModel.fetchWeatherForCities(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar with Logo and Icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // Fixed height to prevent shifting
                .align(Alignment.TopCenter), // Position at the top
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pencil Icon on the Left
            IconButton(onClick = { navController.navigate("edit") }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Icon"
                )
            }

            // Logo in the Middle
            Image(
                painter = painterResource(id = R.drawable.logo), // Replace `logo` with the actual logo name
                contentDescription = "App Logo",
                modifier = Modifier.size(170.dp) // Increased the size of the logo
            )

            // Search Icon on the Right
            IconButton(onClick = { navController.navigate("search") }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            }
        }

        // Content below Top Bar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 96.dp), // Add padding to ensure space for the top bar
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Search Result Display
            if (searchedCityWeather != null) {
                CityWeatherFullScreen(
                    cityWeather = searchedCityWeather!!,
                    currentIndex = 0,
                    totalCities = 1
                )
            } else if (searchQuery.isNotEmpty() && searchedCityWeather == null) {
                Text(
                    text = "No results found for \"$searchQuery\".",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                // Default City Weather Display
                if (citiesWeather.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(citiesWeather) { cityWeather ->
                            Box(
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .fillParentMaxHeight()

                            ) {
                                CityWeatherFullScreen(
                                    cityWeather = cityWeather,
                                    currentIndex = citiesWeather.indexOf(cityWeather),
                                    totalCities = citiesWeather.size
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun CityWeatherFullScreen(
    cityWeather: CityWeather,
    currentIndex: Int,
    totalCities: Int
) {
    val currentWeather = cityWeather.currentWeather
    val forecast = cityWeather.forecast
    val iconCode = currentWeather.weather[0].icon
    val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"

    // Wrap the content in a Column with vertical scroll enabled
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 5.dp, horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF87CEEB)) // Light blue background
            .border(4.dp, Color(0xFF00BFFF), RoundedCornerShape(18.dp))
            .verticalScroll(rememberScrollState()), // Enable vertical scrolling
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp)) // Spacer added above the city name

        Text(
            text = "${currentWeather.name}, ${cityWeather.forecast.city.country}",
            fontSize = 24.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${currentWeather.main.temp.toInt()}°C",
            fontSize = 48.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Feels like ${currentWeather.main.feels_like.toInt()}°C",
            fontSize = 18.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = currentWeather.weather[0].description.replaceFirstChar { it.uppercase() },
            fontSize = 20.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = rememberImagePainter(iconUrl),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(60.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Humidity", fontSize = 16.sp, color = Color.Black)
                Text(text = "${currentWeather.main.humidity}%", fontSize = 18.sp, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Wind", fontSize = 16.sp, color = Color.Black)
                Text(text = "${(currentWeather.wind.speed * 3.6).toInt()} km/h", fontSize = 18.sp, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Pressure", fontSize = 16.sp, color = Color.Black)
                Text(text = "${currentWeather.main.pressure} hPa", fontSize = 18.sp, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Visibility", fontSize = 16.sp, color = Color.Black)
                Text(text = "${currentWeather.visibility / 1000} km", fontSize = 18.sp, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Sunrise", fontSize = 16.sp, color = Color.Black)
                Text(text = formatUnixTime(currentWeather.sys.sunrise, currentWeather.timezone), fontSize = 18.sp, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Sunset", fontSize = 16.sp, color = Color.Black)
                Text(text = formatUnixTime(currentWeather.sys.sunset, currentWeather.timezone), fontSize = 18.sp, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hourly Forecast",
            fontSize = 20.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(forecast.list.take(8)) { forecastData ->
                ForecastCard(forecastData = forecastData)
            }
        }
    }
}


@Composable
fun ForecastCard(forecastData: ForecastResponse.ForecastData) {
    val iconCode = forecastData.weather[0].icon
    val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"

    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF87CEEB))
            .border(2.dp, Color(0xFF00BFFF), RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = forecastData.dt_txt.split(" ")[1],
            fontSize = 14.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${forecastData.main.temp.toInt()}°C",
            fontSize = 18.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = rememberImagePainter(iconUrl),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun CityIndexIndicator(currentIndex: Int, totalCities: Int) {
    val cityIndicator = "•"

    Text(
        text = "City $cityIndicator (${currentIndex + 1} of $totalCities)",
        fontSize = 14.sp,
        color = Color.Black,
        fontWeight = FontWeight.Bold
    )
}

fun formatUnixTime(unixTime: Long, timezoneOffset: Int): String {
    val date = Date((unixTime + timezoneOffset) * 1000L)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}
