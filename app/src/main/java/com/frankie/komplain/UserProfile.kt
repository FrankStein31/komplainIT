package com.frankie.komplain

data class UserProfile(
    val nik: String,
    val nama: String,
    val email: String,
    val noHp: String,
    val token: String? = null
)