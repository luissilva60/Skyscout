package com.example.skyscout.data.models


data class CityWeather(
    val currentWeather: WeatherResponse,
    val forecast: ForecastResponse
)