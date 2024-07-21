package com.frankie.komplain

data class RatingSubmit(
    val rating: Int
)

//data class DetailRatingSubmit(
//    val responsivitas: Boolean,
//    val komunikasi: Boolean,
//    val sikap: Boolean,
//    val waktu: Boolean,
//    val pemahaman: Boolean,
//    val desc_rating : String
//)

data class DetailRatingSubmit(
    val responsivitas: Int,
    val komunikasi: Int,
    val sikap: Int,
    val waktu: Int,
    val pemahaman: Int,
    val desc_rating: String
)