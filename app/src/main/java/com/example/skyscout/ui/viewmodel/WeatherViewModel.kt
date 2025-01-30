package com.example.skyscout.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyscout.data.models.CityWeather
import com.example.skyscout.data.models.FavoritePlace
import com.example.skyscout.data.models.FavoritesResponse
import com.example.skyscout.data.models.UserSession
import com.example.skyscout.network.ApiService
import com.example.skyscout.network.DatabaseApiService
import com.example.skyscout.network.DatabaseRetrofitInstance
import com.example.skyscout.ui.screens.fetchUserLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val _citiesWeather = MutableStateFlow<List<CityWeather>>(emptyList())
    val citiesWeather: StateFlow<List<CityWeather>> = _citiesWeather

    private val _searchedCityWeather = MutableStateFlow<CityWeather?>(null)
    val searchedCityWeather: StateFlow<CityWeather?> = _searchedCityWeather

    // Fetch weather for user's favorite cities
    fun fetchWeatherForCities(context: Context) {
        viewModelScope.launch {
            try {
                // Fetch favorite places for the user

                val favoritePlacesResponse = DatabaseRetrofitInstance.instance.getFavorites(
                    UserSession.currentUser?._id.toString()
                )
                if (!favoritePlacesResponse.isSuccessful) {
                    Log.e("FavoritePlaces", "Failed to fetch favorite places: ${favoritePlacesResponse.message()}")
                    return@launch
                }

                val favoritePlaces: FavoritesResponse? = favoritePlacesResponse.body()
                Log.d("FavoritePlaces", "Fetched favorite places: $favoritePlaces")

                // Fetch user location weather
                val userWeather = try {
                    val userLocation = fetchUserLocation(context)
                    userLocation?.let {
                        val currentWeather = ApiService.weatherApi.getWeather(
                            latitude = it.latitude,
                            longitude = it.longitude
                        ).let { weatherResponse ->
                            weatherResponse.copy(name = "My Location")
                        }
                        val forecast = ApiService.weatherApi.getForecast(
                            latitude = it.latitude,
                            longitude = it.longitude
                        )
                        Log.d("UserWeatherData", "Received weather data for user location: $currentWeather")
                        Log.d("UserForecastData", "Received forecast data for user location: $forecast")

                        CityWeather(currentWeather, forecast)
                    }
                } catch (e: Exception) {
                    Log.e("UserWeatherData", "Error fetching user location weather", e)
                    null
                }

                // Fetch weather for favorite cities
                val weatherData = favoritePlaces?.favorites?.mapNotNull { favorite ->
                    try {
                        val geocode = ApiService.weatherApi.getCoordinates(cityName = favorite.placeName)
                        if (geocode.isNotEmpty()) {
                            val currentWeather = ApiService.weatherApi.getWeather(
                                latitude = geocode[0].lat,
                                longitude = geocode[0].lon
                            )
                            val forecast = ApiService.weatherApi.getForecast(
                                latitude = geocode[0].lat,
                                longitude = geocode[0].lon
                            )
                            Log.d("WeatherData", "Received weather data for city: ${favorite.placeName} - $currentWeather")
                            Log.d("ForecastData", "Received forecast data for city: ${favorite.placeName} - $forecast")

                            CityWeather(currentWeather, forecast)
                        } else {
                            Log.w("WeatherData", "No coordinates found for city: ${favorite.placeName}")
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("WeatherData", "Error fetching weather for city: ${favorite.placeName}", e)
                        null
                    }
                }

                // Combine user location weather with fetched city weather
                val combinedWeatherData = mutableListOf<CityWeather>()
                userWeather?.let { combinedWeatherData.add(it) }
                if (weatherData != null) {
                    combinedWeatherData.addAll(weatherData)
                }
                _citiesWeather.value = combinedWeatherData

            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather for cities", e)
            }
        }
    }

    fun fetchWeatherForCity(cityName: String) {
        viewModelScope.launch {
            try {
                val geocode = ApiService.weatherApi.getCoordinates(cityName = cityName)
                if (geocode.isNotEmpty()) {
                    val currentWeather = ApiService.weatherApi.getWeather(
                        latitude = geocode[0].lat,
                        longitude = geocode[0].lon
                    )
                    val forecast = ApiService.weatherApi.getForecast(
                        latitude = geocode[0].lat,
                        longitude = geocode[0].lon
                    )

                    Log.d("WeatherSearch", "Received weather data for city: $cityName - $currentWeather")
                    Log.d("ForecastSearch", "Received forecast data for city: $cityName - $forecast")

                    _searchedCityWeather.value = CityWeather(currentWeather, forecast)
                } else {
                    Log.w("WeatherSearch", "No coordinates found for city: $cityName")
                    _searchedCityWeather.value = null
                }
            } catch (e: Exception) {
                Log.e("WeatherSearch", "Error fetching weather for city: $cityName", e)
                _searchedCityWeather.value = null
            }
        }
    }

    fun fetchWeatherForCityCoordinates(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val currentWeather = ApiService.weatherApi.getWeather(latitude = latitude, longitude = longitude)
                val forecast = ApiService.weatherApi.getForecast(latitude = latitude, longitude = longitude)

                _searchedCityWeather.value = CityWeather(currentWeather, forecast)
            } catch (e: Exception) {
                Log.e("WeatherSearch", "Error fetching weather for coordinates ($latitude, $longitude)", e)
                _searchedCityWeather.value = null
            }
        }
    }

    fun clearSearchResult() {
        _searchedCityWeather.value = null
    }
}
