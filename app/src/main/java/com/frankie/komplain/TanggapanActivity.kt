package com.frankie.komplain

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frankie.komplain.services.ApiClient
import com.frankie.komplain.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TanggapanActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tanggapan)
        getSupportActionBar()?.hide()

        apiService = ApiClient.apiService

        val pengaduanId = intent.getIntExtra("pengaduanId", 0)
        if (pengaduanId != 0) {
            fetchTanggapan(pengaduanId)
        }
    }

    private fun fetchTanggapan(pengaduanId: Int) {
        val call = apiService.getTanggapanByPengaduanId(pengaduanId)
        call.enqueue(object : Callback<List<Tanggapan>> {
            override fun onResponse(call: Call<List<Tanggapan>>, response: Response<List<Tanggapan>>) {
                if (response.isSuccessful) {
                    val tanggapanList = response.body()
                    if (tanggapanList != null && tanggapanList.isNotEmpty()) {
                        showTanggapan(tanggapanList)
                    } else {
                        showNoTanggapanMessage()
                    }
                } else {
                    showErrorMessage()
                }
            }
            override fun onFailure(call: Call<List<Tanggapan>>, t: Throwable) {
                showErrorMessage()
            }
        })
    }

    private fun showTanggapan(tanggapanList: List<Tanggapan>) {
        val sortedTanggapanList = tanggapanList.sortedByDescending { it.created_at }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = TanggapanAdapter(sortedTanggapanList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun showNoTanggapanMessage() {
        val noTanggapanMessage = findViewById<TextView>(R.id.noTanggapanTextView)
        noTanggapanMessage.visibility = View.VISIBLE

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.visibility = View.GONE

        noTanggapanMessage.text = "Belum ada tanggapan"
    }

    private fun showErrorMessage() {
        Toast.makeText(this, "Terjadi kesalahan dalam mengambil data", Toast.LENGTH_SHORT).show()
    }
}