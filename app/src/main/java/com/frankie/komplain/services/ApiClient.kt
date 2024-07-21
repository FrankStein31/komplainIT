package com.frankie.komplain.services

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://192.168.21.169:8000/api/"

    // Buat instance OkHttpClient
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    val apiService: ApiService by lazy {
        createApiService()
    }

    private fun createApiService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Gunakan OkHttpClient yang telah dibuat
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
