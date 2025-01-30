package com.example.skyscout.network
import com.example.skyscout.data.models.FavoritePlace
import com.example.skyscout.data.models.FavoritesResponse
import com.example.skyscout.data.models.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

data class LoginResponse(val message: String, val user: User, val favorites: List<FavoritePlace>)
data class GenericResponse(val message: String)

interface DatabaseApiService {
    @GET("/users")
    suspend fun getUsers(): Response<List<User>>

    @POST("/users/createUser")
    suspend fun createUser(@Body body: Map<String, String>): Response<GenericResponse>

    @POST("/users/login")
    suspend fun login(@Body body: Map<String, String>): Response<LoginResponse>

    @POST("/users/addFavorite")
    suspend fun addFavorite(@Body body: Map<String, String>): Response<GenericResponse>


    @DELETE("/users/deleteFavorite")
    suspend fun deleteFavorite(
        @Query("favoriteId") favoriteId: String
    ): Response<ResponseBody>




    @GET("/users/favorites/{placeUserId}")
    suspend fun getFavorites(@Path("placeUserId") userId: String): Response<FavoritesResponse>

}
