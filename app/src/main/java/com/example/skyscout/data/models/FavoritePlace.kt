package com.example.skyscout.data.models

data class FavoritePlace(
    val _id: String,
    val placeName: String,
    val placeLat: Double,
    val placeLong: Double,
    val placeUserId: Int // Foreign key linking to User
)
