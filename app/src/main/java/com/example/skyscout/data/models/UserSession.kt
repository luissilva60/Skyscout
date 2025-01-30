package com.example.skyscout.data.models


import com.example.skyscout.data.models.FavoritePlace
import com.example.skyscout.data.models.User

object UserSession {
    var currentUser: User? = null
    var favorites: List<FavoritePlace> = emptyList()
}