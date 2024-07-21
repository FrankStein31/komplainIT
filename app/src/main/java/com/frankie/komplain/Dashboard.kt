package com.frankie.komplain

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.app.AlertDialog
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frankie.komplain.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Dashboard : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: DashboardPengaduanAdapter
    private lateinit var logologinDashboard: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        getSupportActionBar()?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //logo refresh
        logologinDashboard = findViewById(R.id.logologinDashboard)
        logologinDashboard.setOnClickListener{
            val nik = intent.getStringExtra("nik")
            intent.putExtra("nik", nik)
            val tokenku = sharedPreferences.getString("token", "")
            Log.d("NIK", "Nilai NIK adalah KU: $nik")
            Log.d("NIK", "Nilai token adalah: $tokenku")
            val intent = Intent(this, Dashboard::class.java)
            intent.putExtra("nik", nik)
            startActivity(intent)
        }

        //recycle view untuk pengaduan yang tidak selesai/ongoing
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDashboard)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = DashboardPengaduanAdapter(arrayListOf(), this)
        recyclerView.adapter = adapter

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.21.169:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val nik = intent.getStringExtra("nik")
        intent.putExtra("nik", nik)
        getPengaduanByUserNikStatusTidakSelesai(apiService, nik, adapter)

        //tambah pengaduan
        val cardView = findViewById<CardView>(R.id.cardview2)
        cardView.setOnClickListener {
            val intent = Intent(this, pengaduan::class.java)
            startActivity(intent)
        }

        //list pengaduan
        val cardlist = findViewById<CardView>(R.id.cardview)
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        cardlist.setOnClickListener {
            val nik = intent.getStringExtra("nik")
            intent.putExtra("nik", nik)
            val tokenku = sharedPreferences.getString("token", "")
            Log.d("NIK", "Nilai NIK adalah KU: $nik")
            Log.d("NIK", "Nilai token adalah: $tokenku")
            val intent = Intent(this, ListKomplain::class.java)
            intent.putExtra("nik", nik)
            startActivity(intent)
        }

        //profile
        val cardProfile = findViewById<CardView>(R.id.cardview3)
        cardProfile.setOnClickListener {
            // Lakukan panggilan ke API untuk mendapatkan profil pengguna
            val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val nik = intent.getStringExtra("nik")
            val tokenku = sharedPreferences.getString("token", "")
            // Buat instance dari Retrofit untuk panggilan API
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.21.169:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            // Buat instance dari ApiService
            val apiService = retrofit.create(ApiService::class.java)
            // Lakukan panggilan untuk mendapatkan profil pengguna
            val call = apiService.getUserProfile(nik ?: "")
            call.enqueue(object : Callback<UserProfile> {
                override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                    if (response.isSuccessful) {
                        val userProfile = response.body()
                        // Tampilkan alert dialog dengan informasi profil pengguna
                        userProfile?.let {
                            val dialogView = LayoutInflater.from(this@Dashboard).inflate(R.layout.dialog_profile, null)
                            dialogView.findViewById<TextView>(R.id.tvNik).text = "NIK : ${it.nik}"
                            dialogView.findViewById<TextView>(R.id.tvNama).text = "Nama : ${it.nama}"
                            dialogView.findViewById<TextView>(R.id.tvEmail).text = "Email : ${it.email}"
                            dialogView.findViewById<TextView>(R.id.tvPhone).text = "Nomor HP : ${it.noHp}"
                            dialogView.findViewById<TextView>(R.id.tvToken).text = "Token : $tokenku"

                            val alertDialogBuilder = AlertDialog.Builder(this@Dashboard)
                            alertDialogBuilder.apply {
                                setView(dialogView)
                                setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                                setCancelable(false)
                                create().show()
                            }
                        }
                    } else {
                        // Tangani respon gagal dari panggilan API
                    }
                }
                override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                    // Tangani kesalahan saat melakukan panggilan API
                    t.printStackTrace()
                }
            })
        }

        //logout
        val cardViewLogout = findViewById<CardView>(R.id.cardViewLogout)
        cardViewLogout.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this@Dashboard)
            alertDialogBuilder.apply {
                setTitle("Logout")
                setMessage("Apakah anda yakin ingin logout?")
                setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    redirectToLogin()
                }
                setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                setCancelable(false)
                create().show()
            }
        }

    }

    private fun getPengaduanByUserNikStatusTidakSelesai(apiService: ApiService, nik: String?, adapter: DashboardPengaduanAdapter) {
        val call = apiService.getPengaduanByUserNikStatusTidakSelesai(nik ?: "")
        Log.d("NIK RecyclerVIew", "Nilai NIK adalah KU: $nik")
        call.enqueue(object : Callback<ArrayList<ListPengadaan>> {
            override fun onResponse(
                call: Call<ArrayList<ListPengadaan>>,
                response: Response<ArrayList<ListPengadaan>>
            ) {
                if (response.isSuccessful) {
                    showNoPengaduanMessage(false)
                    val pengaduanList = response.body()
                    if (pengaduanList != null) {
                        adapter.setData(pengaduanList)
                    }
                } else {
                    showNoPengaduanMessage(true)
                    Log.e("Dashboard", "Gagal mendapatkan data pengaduan: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<ListPengadaan>>, t: Throwable) {
                Log.e("Dashboard recycle", "API gagal")
                t.printStackTrace()
            }
        })
    }

    private fun showNoPengaduanMessage(show: Boolean) {
        val PengaduanSelesaiTextView = findViewById<TextView>(R.id.PengaduanSelesaiTextView)
        val recyclerViewDashboard = findViewById<RecyclerView>(R.id.recyclerViewDashboard)
        if (show) {
            PengaduanSelesaiTextView.visibility = View.VISIBLE
            recyclerViewDashboard.visibility = View.GONE
            PengaduanSelesaiTextView.text = "Pengaduan yang diajukan sudah selesai"
        } else {
            PengaduanSelesaiTextView.visibility = View.GONE
            recyclerViewDashboard.visibility = View.VISIBLE
        }
    }

    private fun redirectToLogin() {
        // Mengarahkan pengguna ke layar login
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        Toast.makeText(this@Dashboard, "Logout Berhasil", Toast.LENGTH_SHORT).show()
        // Menyelesaikan aktivitas saat ini agar pengguna tidak dapat kembali menggunakan tombol back
        finish()
    }

}
