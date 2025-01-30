package com.example.skyscout.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DatabaseRetrofitInstance {
    private const val BASE_URL = "http:/10.0.2.2:3000"

    val instance: DatabaseApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabaseApiService::class.java)
    }
}
