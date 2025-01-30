package com.example.skyscout.data.models

import com.example.skyscout.data.models.Location

data class CurrentWeatherLocationInformation(
    val location: Location,
    val currentWeather: CurrentWeather,
    val dailyForecast: List<DailyForecast>
)



data class CurrentWeather(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<WeatherDescription>
)

data class WeatherDescription(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class DailyForecast(
    val temp: Double,
    val weather: List<WeatherDescription>
)
