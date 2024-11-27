package com.example.skyscout.data.models

data class FavoritePlace(
    val placeId: String,
    val placeName: String,
    val placeLat: Double,
    val placeLong: Double,
    val placeUserId: String // Foreign key linking to User
)
