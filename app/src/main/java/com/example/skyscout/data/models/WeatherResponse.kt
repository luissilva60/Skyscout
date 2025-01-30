package com.example.skyscout.data.models


data class WeatherResponse(
    val coord: Coordinates,
    val weather: List<Weather>,
    val base: String,                // Base information (e.g., "stations")
    val main: MainWeather,
    val visibility: Int,             // Visibility in meters
    val wind: Wind,
    val clouds: Clouds,              // Cloudiness percentage
    val dt: Long,                    // Data calculation time (UNIX timestamp)
    val sys: Sys,
    val timezone: Int,               // Timezone offset in seconds
    val id: Int,                     // City ID
    val name: String,                // City name
    val cod: Int                     // Response code
) {
    data class Coordinates(
        val lon: Double,             // Longitude
        val lat: Double              // Latitude
    )

    data class Weather(
        val id: Int,                 // Weather condition ID
        val main: String,            // Group of weather parameters
        val description: String,     // Weather condition description
        val icon: String             // Weather icon ID
    )

    data class MainWeather(
        val temp: Double,            // Current temperature
        val feels_like: Double,      // Feels-like temperature
        val temp_min: Double,        // Minimum temperature
        val temp_max: Double,        // Maximum temperature
        val pressure: Int,           // Atmospheric pressure
        val humidity: Int,           // Humidity percentage
        val sea_level: Int,          // Sea level pressure
        val grnd_level: Int          // Ground level pressure
    )

    data class Wind(
        val speed: Double,           // Wind speed in m/s
        val deg: Int                 // Wind direction in degrees
    )

    data class Clouds(
        val all: Int                 // Cloudiness percentage
    )

    data class Sys(
        val type: Int,               // Internal parameter
        val id: Int,                 // Internal parameter
        val country: String,         // Country code (e.g., "PL")
        val sunrise: Long,           // Sunrise time (UNIX timestamp)
        val sunset: Long             // Sunset time (UNIX timestamp)
    )
}
