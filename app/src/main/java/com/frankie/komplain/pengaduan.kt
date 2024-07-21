package com.frankie.komplain

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.frankie.komplain.databinding.ActivityPengaduanBinding
import com.frankie.komplain.services.ApiService
import com.frankie.komplain.services.FileUtil
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class pengaduan : AppCompatActivity() {
    private lateinit var binding: ActivityPengaduanBinding
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengaduanBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        supportActionBar?.hide()

        clickListeners()
    }

    private fun clickListeners() {
        binding.btnChooseImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(intent, REQUEST_IMAGE_PICK)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
        }

        binding.buttonSubmit.setOnClickListener {
            val description = binding.description.text.toString()
            val jenisPengaduan = binding.coursesspinner.selectedItem.toString()

            if (description.isNotEmpty() && jenisPengaduan.isNotEmpty()) {
                getToken()?.let { token ->
                    submitComplaint(description, jenisPengaduan, token) } ?: run {
                    Toast.makeText(this@pengaduan, "Token tidak ditemukan!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@pengaduan, "Harap isi semua bidang", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getToken(): String? {
        val sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }

    private fun submitComplaint(description: String, jenisPengaduan: String, token: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.21.169:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val file = if (imagePath != null) File(imagePath!!) else null
        val requestFile = if (file != null) RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file) else null
        val body = if (requestFile != null) {
            MultipartBody.Part.createFormData("image", file?.name, requestFile)
        } else null
        val descriptionBody = RequestBody.create(okhttp3.MultipartBody.FORM, description)
        val jenisPengaduanBody = RequestBody.create(okhttp3.MultipartBody.FORM, jenisPengaduan)
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.submitComplaint(token, descriptionBody, body, jenisPengaduanBody)
        call.enqueue(object : Callback<CreateKomplaint> {
            override fun onResponse(call: Call<CreateKomplaint>, response: Response<CreateKomplaint>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@pengaduan, "Pengaduan berhasil diajukan", Toast.LENGTH_SHORT).show()
                    clearFields()
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        val jsonObject = JSONObject(errorBody)
                        val errorMessage = jsonObject.getString("error")
                        Log.e("PengajuanJson", "Gagal mengajukan pengaduan : ${response.code()} - $errorMessage")
                        Toast.makeText(this@pengaduan, "Gagal mengajukan pengaduan: $errorMessage", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e("PengajuanAndro", "Gagal mengajukan pengaduan : ${response.code()}")
                        Toast.makeText(this@pengaduan, "Gagal mengajukan pengaduan", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<CreateKomplaint>, t: Throwable) {
                Toast.makeText(this@pengaduan, "Error, Gagal mengajukan pengaduan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearFields() {
        binding.description.text.clear()
        binding.imageView.setImageResource(R.drawable.upload)
        imagePath = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(intent, REQUEST_IMAGE_PICK)
            } else {
                Toast.makeText(
                    this,
                    "Izin akses media ditolak",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            val imageView = binding.imageView

            selectedImageUri?.let {
                imagePath = FileUtil.getRealPathFromUri(this, it)
                val bitmap = BitmapFactory.decodeFile(imagePath)
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        private const val REQUEST_IMAGE_PICK = 100
    }
}