package com.example.skyscout.network
import com.example.skyscout.data.models.ForecastResponse
import com.example.skyscout.data.models.GeocodingResponse
import com.example.skyscout.data.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("geo/1.0/direct")
    suspend fun getCoordinates(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String = "ea22e43228003f29100ce1317db4d137"
    ): List<GeocodingResponse>

    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = "ea22e43228003f29100ce1317db4d137",
        @Query("units") units: String = "metric" // Fetch temperatures in Celsius
    ): WeatherResponse

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = "ea22e43228003f29100ce1317db4d137",
        @Query("units") units: String = "metric" // Fetch temperatures in Celsius
    ): ForecastResponse

}
