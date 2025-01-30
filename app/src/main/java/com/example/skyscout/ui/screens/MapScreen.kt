package com.example.skyscout.ui.screens

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skyscout.R
import com.example.skyscout.ui.viewmodels.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(viewModel: WeatherViewModel = viewModel()) {
    var markerLocation by remember { mutableStateOf<LatLng?>(null) }
    val searchedCityWeather by viewModel.searchedCityWeather.collectAsState()

    var showWeatherCard by remember { mutableStateOf(false) }

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    val initialLatLng = LatLng(53.428543, 14.552812) // Replace with your desired latitude and longitude
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLatLng, 12f) // Adjust zoom level as needed
    }

    val context = LocalContext.current

    // Initialize location client
    val fusedLocationProviderClient: FusedLocationProviderClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }

    // Fetch current location on first launch
    LaunchedEffect(Unit) {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                currentLocation = LatLng(it.latitude, it.longitude)
                // Center the camera on the current location
                cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation!!, 13.5f)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                markerLocation = latLng
                viewModel.fetchWeatherForCityCoordinates(latLng.latitude, latLng.longitude)
                showWeatherCard = true
            },
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true, // Enables zoom buttons
                myLocationButtonEnabled = true, // Enables the "My Location" button
                compassEnabled = true // Enables the compass on the map
            ),
            properties = com.google.maps.android.compose.MapProperties(
                isMyLocationEnabled = true // Shows the default blue location indicator
            )
        )

        currentLocation?.let {
            /*androidx.compose.material3.FloatingActionButton(
                onClick = {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
                },
                modifier = Modifier
                    .align(Alignment.BottomStart) // Align to the bottom end (right)
                    .padding(16.dp)
                    .padding(start = 10.dp), // Additional padding to move it further to the right
                containerColor = Color(0xFF87CEEB)
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.location), // Replace with your drawable's name
                    contentDescription = "My Location",
                    modifier = Modifier.size(24.dp), // Adjust size as needed
                )
            }*/
        }


        // Display a prompt to click anywhere on the map to show the weather
        if (!showWeatherCard) {
            Text(
                text = "Click anywhere on the map to show the weather",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(end = 60.dp)
                    .padding(start = 5.dp)
                    .padding(bottom = 50.dp)
                    .background(Color(0xFF87CEEB).copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Weather card displayed when a location is selected
        if (showWeatherCard && searchedCityWeather != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(top= 16.dp, bottom = 16.dp)
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.97f), // Increased card height
                shape = RoundedCornerShape(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF87CEEB))
                        .padding(top=20.dp,bottom = 20.dp)// Reduced padding for better spacing
                ) {
                    IconButton(
                        onClick = { showWeatherCard = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(y = (-16).dp, x = (-8).dp)
                            .padding() // Negative top padding to move it upwards
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Black
                        )
                    }


                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 24.dp)
                            .background(Color(0xFF87CEEB)), // Adjusted padding for weather content
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CityWeatherFullScreen(
                            cityWeather = searchedCityWeather!!,
                            currentIndex = 0,
                            totalCities = 1
                        )
                    }
                }
            }
        }
    }
}