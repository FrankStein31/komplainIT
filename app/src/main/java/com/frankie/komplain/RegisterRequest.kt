package com.frankie.komplain

data class RegisterRequest(
    val nik: String,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val password_confirmation: String
)
