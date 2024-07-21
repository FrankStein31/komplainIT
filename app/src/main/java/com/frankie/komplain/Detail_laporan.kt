package com.frankie.komplain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.frankie.komplain.services.ApiClient
import com.frankie.komplain.services.ApiService
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class Detail_laporan : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var ratingBar: RatingBar
    private var pengaduanId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_detail_laporan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        apiService = ApiClient.apiService
        ratingBar = findViewById(R.id.ratingBar)
        pengaduanId = intent.getIntExtra("id", 0)

        Log.d("ID", "Lihat Id: $pengaduanId")

        val btnTanggapan = findViewById<Button>(R.id.btnTanggapan)
        btnTanggapan.setOnClickListener{
            val intent = Intent(this@Detail_laporan, TanggapanActivity::class.java)
            intent.putExtra("pengaduanId", pengaduanId)
            startActivity(intent)
        }

        if (pengaduanId != 0) {
            fetchPengaduanDetail(pengaduanId)
        }

        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        val buttons = listOf(
            findViewById<MaterialButton>(R.id.btnResponsivitas),
            findViewById<MaterialButton>(R.id.btnKomunikasi),
            findViewById<MaterialButton>(R.id.btnSikapPetugas),
            findViewById<MaterialButton>(R.id.btnWaktuPenanganan),
            findViewById<MaterialButton>(R.id.btnPemahamanMasalah)
        )
        val ratingBars = listOf(
            findViewById<RatingBar>(R.id.ratingBarResponsivitas),
            findViewById<RatingBar>(R.id.ratingBarKomunikasi),
            findViewById<RatingBar>(R.id.ratingBarSikapPetugas),
            findViewById<RatingBar>(R.id.ratingBarWaktuPenanganan),
            findViewById<RatingBar>(R.id.ratingBarPemahamanMasalah)
        )

//        buttons.forEach { button ->
//            button.setOnClickListener {
//                val strokeColor = button.strokeColor?.defaultColor
//                val tealColor = getColor(R.color.teal_700)
//
//                if (strokeColor == tealColor) {
//                    button.strokeColor = getColorStateList(R.color.abuAbu)
//                } else {
//                    button.strokeColor = getColorStateList(R.color.teal_700)
//                }
//            }
//        }

//        buttons.forEachIndexed { index, button ->
//            button.setOnClickListener {
//                if (ratingBars[index].rating > 0) {
//                    button.strokeColor = getColorStateList(R.color.teal_700)
//                } else {
//                    button.strokeColor = getColorStateList(R.color.abuAbu)
//                }
//            }
//        }

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                val currentRating = ratingBars[index].rating
                if (currentRating > 0) {
                    ratingBars[index].rating = 0f
                    button.strokeColor = getColorStateList(R.color.abuAbu)
                } else {
                    ratingBars[index].rating = ratingBars[index].numStars.toFloat()
                    button.strokeColor = getColorStateList(R.color.teal_700)
                }
            }
        }

        ratingBars.forEachIndexed { index, ratingBar ->
            ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
                if (rating > 0) {
                    buttons[index].strokeColor = getColorStateList(R.color.teal_700)
                } else {
                    buttons[index].strokeColor = getColorStateList(R.color.abuAbu)
                }
            }
        }

        val submitButton = findViewById<MaterialButton>(R.id.submitdetailrate)
        submitButton.setOnClickListener {
//            val responsivitas = findViewById<MaterialButton>(R.id.btnResponsivitas).strokeColor?.defaultColor == getColor(R.color.teal_700)
//            val komunikasi = findViewById<MaterialButton>(R.id.btnKomunikasi).strokeColor?.defaultColor == getColor(R.color.teal_700)
//            val sikap = findViewById<MaterialButton>(R.id.btnSikapPetugas).strokeColor?.defaultColor == getColor(R.color.teal_700)
//            val waktu = findViewById<MaterialButton>(R.id.btnWaktuPenanganan).strokeColor?.defaultColor == getColor(R.color.teal_700)
//            val pemahaman = findViewById<MaterialButton>(R.id.btnPemahamanMasalah).strokeColor?.defaultColor == getColor(R.color.teal_700)
//            val ratingComment = findViewById<EditText>(R.id.etRatingComment).text.toString()

            val responsivitas = ratingBars[0].rating.toInt()
            val komunikasi = ratingBars[1].rating.toInt()
            val sikap = ratingBars[2].rating.toInt()
            val waktu = ratingBars[3].rating.toInt()
            val pemahaman = ratingBars[4].rating.toInt()
            val ratingComment = findViewById<EditText>(R.id.etRatingComment).text.toString()

            val detailRating = DetailRatingSubmit(responsivitas, komunikasi, sikap, waktu, pemahaman, ratingComment)

            apiService.submitDetailRating(pengaduanId, detailRating).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@Detail_laporan, "Detail rating berhasil dikirim.", Toast.LENGTH_SHORT).show()
                        refreshActivity()
                    } else {
                        Toast.makeText(this@Detail_laporan, "Gagal mengirim detail rating.", Toast.LENGTH_SHORT).show()
                        Log.e("Gagal mengirim detail rating", "Response code: " + response.code() + " Response message: " + response.message() + ", data: " + detailRating)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@Detail_laporan, "Error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // Fungsi untuk mengambil detail pengaduan dari server
    private fun fetchPengaduanDetail(id: Int) {
        apiService.getDetailPengaduan(id).enqueue(object : Callback<DetailPengadaan> {
            override fun onResponse(call: Call<DetailPengadaan>, response: Response<DetailPengadaan>) {
                if (response.isSuccessful) {
                    val detailPengadaan: DetailPengadaan? = response.body()
                    // Bind data yang diperoleh dari API ke UI
                    bindDataToUI(detailPengadaan)
                } else {
                    // Tangani respon gagal
                    Toast.makeText(this@Detail_laporan, "Failed to fetch detail", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DetailPengadaan>, t: Throwable) {
                // Tangani kegagalan panggilan
                Toast.makeText(this@Detail_laporan, "Error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("Error", "Error : ${t.message}")
            }
        })
    }

    // Fungsi untuk mengikat data ke UI setelah mendapatkan respons dari server
    private fun bindDataToUI(detailPengadaan: DetailPengadaan?) {
        // Memastikan detailPengadaan tidak null
        detailPengadaan?.let {
            // Bind ImageView (dengan asumsi Anda menggunakan Glide untuk memuat gambar)
            Glide.with(this@Detail_laporan)
                .load("http://192.168.21.169:8000/storage/${it.image}")
                .into(findViewById(R.id.imageView3))
            findViewById<TextView>(R.id.name_value).text = it.name
            findViewById<TextView>(R.id.tglvalue).text = formatDate(it.created_at)
            findViewById<TextView>(R.id.statusvalue).text = it.status
            findViewById<TextView>(R.id.pengaduanvalue).text = it.jenis_pengaduan
            findViewById<TextView>(R.id.descriptionvalue).text = it.description
            findViewById<TextView>(R.id.etRatingComment).text = it.desc_rating
            // Bind nilai rating ke RatingBar
            ratingBar.rating = it.rating.toFloat()
            // Mengirim rating yang diatur oleh pengguna
            ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
                Log.d("Detail_laporan", "Rating changed: $rating")
                val ratingSubmit = RatingSubmit(rating.toInt())
                apiService.submitRating(pengaduanId, ratingSubmit).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            // Tangani respon berhasil
                            Toast.makeText(this@Detail_laporan, "Rating berhasil dikirim, pastikan deskripsi rating terisi ya", Toast.LENGTH_SHORT).show()
                            refreshActivity()
                        } else {
                            // Tangani respon gagal
                            Toast.makeText(this@Detail_laporan, "Gagal memberi rating", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        // Tangani kegagalan panggilan
                        Toast.makeText(this@Detail_laporan, "Error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("Error", "Error : ${t.message}")
                    }
                })
            }

            findViewById<RatingBar>(R.id.ratingBarResponsivitas).rating = it.responsivitas.toFloat()
            findViewById<RatingBar>(R.id.ratingBarKomunikasi).rating = it.komunikasi.toFloat()
            findViewById<RatingBar>(R.id.ratingBarSikapPetugas).rating = it.sikap.toFloat()
            findViewById<RatingBar>(R.id.ratingBarWaktuPenanganan).rating = it.waktu.toFloat()
            findViewById<RatingBar>(R.id.ratingBarPemahamanMasalah).rating = it.pemahaman.toFloat()

            val buttons = listOf(
                findViewById<MaterialButton>(R.id.btnResponsivitas),
                findViewById<MaterialButton>(R.id.btnKomunikasi),
                findViewById<MaterialButton>(R.id.btnSikapPetugas),
                findViewById<MaterialButton>(R.id.btnWaktuPenanganan),
                findViewById<MaterialButton>(R.id.btnPemahamanMasalah)
            )

            val ratings = listOf(
                it.responsivitas,
                it.komunikasi,
                it.sikap,
                it.waktu,
                it.pemahaman
            )

            buttons.forEachIndexed { index, button ->
                button.strokeColor = getColorStateList(if (ratings[index] > 0) R.color.teal_700 else R.color.abuAbu)
            }

//            findViewById<RatingBar>(R.id.ratingBarResponsivitas).rating = it.responsivitas.toFloat()
//            findViewById<RatingBar>(R.id.ratingBarKomunikasi).rating = it.komunikasi.toFloat()
//            findViewById<RatingBar>(R.id.ratingBarSikapPetugas).rating = it.sikap.toFloat()
//            findViewById<RatingBar>(R.id.ratingBarWaktuPenanganan).rating = it.waktu.toFloat()
//            findViewById<RatingBar>(R.id.ratingBarPemahamanMasalah).rating = it.pemahaman.toFloat()

//            findViewById<RatingBar>(R.id.ratingBarResponsivitas).rating = getRatingValue(it.responsivitas)
//            findViewById<RatingBar>(R.id.ratingBarKomunikasi).rating = getRatingValue(it.komunikasi)
//            findViewById<RatingBar>(R.id.ratingBarSikapPetugas).rating = getRatingValue(it.sikap)
//            findViewById<RatingBar>(R.id.ratingBarWaktuPenanganan).rating = getRatingValue(it.waktu)
//            findViewById<RatingBar>(R.id.ratingBarPemahamanMasalah).rating = getRatingValue(it.pemahaman)
        }
    }

    private fun formatDate(date: Date?): String {
        return if (date != null) {
            val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy - HH:mm:ss", Locale.getDefault())
            dateFormat.format(date)
        } else {
            "Not Found"
        }
    }

    private fun refreshActivity() {
        val intent = intent
        finish() // Menutup activity saat ini
        startActivity(intent) // Me-restart activity dengan intent yang sama
    }
}
