package com.frankie.komplain

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frankie.komplain.services.ApiClient
import com.frankie.komplain.databinding.ActivityListKomplainBinding
import com.frankie.komplain.services.ApiClient.apiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListKomplain : AppCompatActivity() {

    private lateinit var binding: ActivityListKomplainBinding
    private lateinit var adapter: PengaduanAdapter
    private lateinit var nik: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListKomplainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSupportActionBar()?.hide()

        // Set up RecyclerView
        adapter = PengaduanAdapter(arrayListOf(),this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Set up Edge-to-Edge
        enableEdgeToEdge(binding.root)

        // Fetch data from API
        nik = intent.getStringExtra("nik")?: ""
        Log.d("NIKListKomplain", "Nilai NIK adalah: $nik")
        setupButtons()
        getData(nik, null)
    }

    private fun setupButtons() {
        binding.btnSemua.setOnClickListener {
            binding.btnSemua.setBackgroundColor(ContextCompat.getColor(this, R.color.abuAbu))
            binding.btnSedangProses.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
            binding.btnBelumProses.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            getData(nik, null)
        }
        binding.btnSedangProses.setOnClickListener {
            binding.btnSedangProses.setBackgroundColor(ContextCompat.getColor(this, R.color.abuAbu))
            binding.btnSemua.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            binding.btnBelumProses.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            getData(nik, "Sedang Di Proses")
        }
        binding.btnBelumProses.setOnClickListener {
            binding.btnBelumProses.setBackgroundColor(ContextCompat.getColor(this, R.color.abuAbu))
            binding.btnSemua.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            binding.btnSedangProses.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
            getData(nik, "Belum di Proses")
        }
    }

    private fun enableEdgeToEdge(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getData(nik: String, status: String?) {
        val call: Call<ArrayList<ListPengadaan>> = if (status.isNullOrEmpty()) {
            apiService.getPengaduanByNik(nik)
        } else {
            apiService.getPengaduanByNikStatus(nik, status)
        }

        call.enqueue(object : Callback<ArrayList<ListPengadaan>> {
            override fun onResponse(
                call: Call<ArrayList<ListPengadaan>>,
                response: Response<ArrayList<ListPengadaan>>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null && data.isNotEmpty()) {
                        showNoPengaduanMessage(false)
                        setDataAdapter(data)
                        Log.d("ListKomplainNIK", "Nilai NIK adalah: $nik")
                        Log.d("ListKomplain", "Data berhasil diambil: $data")
                    } else {
                        Log.d("ListKomplainNIK", "Nilai NIK adalah: $nik")
                        Log.e("ListKomplain", "Data kosong!")
                    }
                } else {
                    showNoPengaduanMessage(true)
                    val statusMessage = status?.let { " dengan status '$it'" } ?: ""
                    Toast.makeText(this@ListKomplain, "Pengaduan$statusMessage tidak ada", Toast.LENGTH_SHORT).show()
                    Log.d("ListKomplainNIK", "Nilai NIK: $nik")
                    Log.e("ListKomplain", "Gagal mengambil data: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ArrayList<ListPengadaan>>, t: Throwable) {
                Log.e("ListKomplain", "Error: ${t.message}")
            }
        })
    }

    private fun showNoPengaduanMessage(show: Boolean) {
        val noPengaduanMessage = findViewById<TextView>(R.id.noPengaduanTextView)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        if (show) {
            noPengaduanMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            noPengaduanMessage.text = "Data Pengaduan Tidak Ada"
        } else {
            noPengaduanMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setDataAdapter(data: ArrayList<ListPengadaan>) {
        adapter.setData(data)
    }

}