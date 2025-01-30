package com.example.skyscout.data.models
data class ForecastResponse(
    val cod: String,                // Response code, e.g., "200"
    val message: Int,               // API message code
    val cnt: Int,                   // Number of forecast data points
    val list: List<ForecastData>,   // List of forecast data points
    val city: City                  // City information
) {
    data class ForecastData(
        val dt: Long,               // Forecast timestamp
        val main: Main,             // Main weather details
        val weather: List<Weather>, // Weather conditions
        val clouds: Clouds,         // Cloud information
        val wind: Wind,             // Wind information
        val visibility: Int,        // Visibility in meters
        val pop: Double,            // Probability of precipitation
        val sys: Sys,               // Additional system data
        val dt_txt: String          // Date and time of forecast
    )

    data class Main(
        val temp: Double,           // Current temperature
        val feels_like: Double,     // Feels-like temperature
        val temp_min: Double,       // Minimum temperature
        val temp_max: Double,       // Maximum temperature
        val pressure: Int,          // Atmospheric pressure
        val sea_level: Int,         // Sea level pressure
        val grnd_level: Int,        // Ground level pressure
        val humidity: Int,          // Humidity percentage
        val temp_kf: Double         // Internal temperature variable
    )

    data class Weather(
        val id: Int,                // Weather condition ID
        val main: String,           // Group of weather parameters
        val description: String,    // Weather condition description
        val icon: String            // Weather icon ID
    )

    data class Clouds(
        val all: Int                // Cloudiness percentage
    )

    data class Wind(
        val speed: Double,          // Wind speed in m/s
        val deg: Int,               // Wind direction in degrees
        val gust: Double            // Wind gust speed in m/s
    )

    data class Sys(
        val pod: String             // Part of the day (e.g., "n" for night)
    )

    data class City(
        val id: Int,                // City ID
        val name: String,           // City name
        val coord: Coord,           // City coordinates
        val country: String,        // Country code
        val population: Int,        // Population
        val timezone: Int,          // Timezone offset in seconds
        val sunrise: Long,          // Sunrise timestamp
        val sunset: Long            // Sunset timestamp
    )

    data class Coord(
        val lat: Double,            // Latitude
        val lon: Double             // Longitude
    )
}
