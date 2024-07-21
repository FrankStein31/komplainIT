package com.frankie.komplain

import java.sql.Timestamp
import java.util.Date

//data class DetailPengadaan(
//    val name: String,
//    val image: String,
//    val status: String,
//    val rating : Int,
//    val description : String,
//    val jenis_pengaduan: String,
//    val created_at : Date,
//    val responsivitas: Int,
//    val komunikasi: Int,
//    val sikap: Int,
//    val waktu: Int,
//    val pemahaman: Int,
//    val desc_rating : String
//)
data class DetailPengadaan(
    val name: String,
    val image: String,
    val status: String,
    val rating: Int,
    val description: String,
    val jenis_pengaduan: String,
    val created_at: Date,
    val responsivitas: Int,
    val komunikasi: Int,
    val sikap: Int,
    val waktu: Int,
    val pemahaman: Int,
    val desc_rating: String
)