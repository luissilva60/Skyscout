package com.example.skyscout.data.models

data class User(
    val userId: String,
    val userEmail: String,
    val userPassword: String // Note: Use hashed values for security in practice
)
