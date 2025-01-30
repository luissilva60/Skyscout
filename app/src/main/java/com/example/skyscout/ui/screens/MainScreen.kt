package com.example.skyscout.ui.screens
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.tasks.await

import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skyscout.R
import com.example.skyscout.data.models.Location
import com.example.skyscout.ui.viewmodel.DBViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val viewModel: DBViewModel = viewModel()

    // Check and request location permissions
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Launch location request when permission is granted
    LaunchedEffect(Unit) {
        if (locationPermissionState.status.isGranted) {
            fetchUserLocation(context)
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavigationGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination

    val items = listOf(Screen.Home, Screen.Map, Screen.Profile)

    NavigationBar( containerColor = Color(0xFF87CEEB)) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(id = screen.icon),
                        contentDescription = screen.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(screen.title, fontSize = 12.sp) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Avoid multiple copies of the same destination in the back stack
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid adding the same destination to the stack
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = Screen.Home.route, modifier = modifier) {
        composable(Screen.Home.route) { WeatherHomePage(navController = navController) }
        composable(Screen.Map.route) { MapScreen() }
        composable(Screen.Profile.route) { ProfileScreen() }
        composable("search") { SearchScreen(navController = navController) }
        composable("edit") { EditScreen(navController = navController, viewModel = viewModel()) } // Add this line
    }
}

data class ScreenItem(val route: String, val title: String, val icon: Int)

object Screen {
    val Home = ScreenItem("home", "Home", R.drawable.home)
    val Map = ScreenItem("map", "Map", R.drawable.map)
    val Profile = ScreenItem("profile", "Profile", R.drawable.profile)
}
suspend fun fetchUserLocation(context: Context): com.example.skyscout.data.models.Location? {
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    return try {
        // Use Priority for more precise or battery-efficient location
        val priority = Priority.PRIORITY_HIGH_ACCURACY
        val cancellationTokenSource = CancellationTokenSource()

        // Await the current location
        val androidLocation = fusedLocationClient.getCurrentLocation(
            priority,
            cancellationTokenSource.token
        ).await()

        // Log and return the location if found
        if (androidLocation != null) {
            Log.d("LocationDebug", "Fetched location: $androidLocation")
            androidLocation.toCustomLocation()
        } else {
            Log.w("LocationWarning", "Current location is null")
            null
        }
    } catch (e: SecurityException) {
        Log.e("LocationError", "Error fetching user location: Permission denied", e)
        null
    } catch (e: Exception) {
        Log.e("LocationError", "Unknown error fetching user location", e)
        null
    }
}

// Extension function to convert android.location.Location to your custom Location model
fun android.location.Location.toCustomLocation(): com.example.skyscout.data.models.Location {
    return com.example.skyscout.data.models.Location(
        latitude = this.latitude,
        longitude = this.longitude
    )
}