package com.frankie.komplain.services

import com.frankie.komplain.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

public interface ApiService {
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @GET("get-all-pengaduan")
    fun getAllPengaduan(): Call<ArrayList<ListPengadaan>>

    @GET("/api/pengaduan/{nik}")
    fun getPengaduanByNik(@Path("nik") nik: String): Call<ArrayList<ListPengadaan>>

    @GET("/api/pengaduan/status/{nik}")
    fun getPengaduanByNikStatus(@Path("nik") nik: String, @Query("status") status: String): Call<ArrayList<ListPengadaan>>

    @GET("/api/pengaduan/tidakselesai/{nik}")
    fun getPengaduanByUserNikStatusTidakSelesai(@Path("nik") nik: String): Call<ArrayList<ListPengadaan>>

    @GET("/api/detail-pengaduan/{id}")
    fun getDetailPengaduan(@Path("id") id: Int): Call<DetailPengadaan>

    @PUT("/api/pengaduan/{id}/rating")
    fun submitRating(@Path("id") id: Int, @Body ratingSubmit: RatingSubmit): Call<Void>

    @POST("/api/create-pengaduan")
    @Multipart
    fun submitComplaint(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("jenis_pengaduan") jenisPengaduan: RequestBody
    ): Call<CreateKomplaint>

    @GET("/api/tanggapan/{pengaduanId}")
    fun getTanggapanByPengaduanId(@Path("pengaduanId") pengaduanId: Int): Call<List<Tanggapan>>

    @GET("/api/profile/{nik}")
    fun getUserProfile(@Path("nik") nik: String): Call<UserProfile>

    @PUT("/api/pengaduan/{id}/detail-rating")
    fun submitDetailRating(
        @Path("id") id: Int,
        @Body ratingSubmit: DetailRatingSubmit
    ): Call<Void>

}
