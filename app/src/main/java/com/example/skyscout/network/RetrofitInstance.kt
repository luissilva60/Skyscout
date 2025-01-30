package com.example.skyscout.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.openweathermap.org/" // Base URL for OpenWeatherMap API

    // Retrofit instance
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Converts JSON to Kotlin objects
            .build()
    }
}



object ApiService {
    val weatherApi: WeatherApiService by lazy {
        RetrofitInstance.retrofit.create(WeatherApiService::class.java)
    }
}