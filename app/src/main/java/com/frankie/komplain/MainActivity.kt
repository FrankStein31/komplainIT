package com.frankie.komplain

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.frankie.komplain.Dashboard
import com.frankie.komplain.ListKomplain
import com.frankie.komplain.R
import com.frankie.komplain.services.ApiClient
import com.frankie.komplain.LoginRequest
import com.frankie.komplain.LoginResponse
import com.frankie.komplain.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var edEmail: EditText
    private lateinit var edPassword: EditText
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.hide()

        edEmail = findViewById(R.id.edEmail)
        edPassword = findViewById(R.id.edPassword)
        val btnSignIn: Button = findViewById(R.id.btnSignIn)
        val txSignUp: TextView = findViewById(R.id.txSignUp)

        txSignUp.setOnClickListener{
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnSignIn.setOnClickListener {
            val email = edEmail.text.toString()
            val password = edPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@MainActivity, "Mohon isi keduanya email dan password.", Toast.LENGTH_SHORT).show()
            } else {
                val loginRequest = LoginRequest(email, password)
                login(loginRequest)
            }
        }
        //fungsi show dan hide password
        val ivShowHidePwd: ImageView = findViewById(R.id.iv_show_hide_pwd)
        ivShowHidePwd.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                // Jika password sebelumnya disembunyikan, tampilkan sebagai teks biasa
                edPassword.transformationMethod = null
                ivShowHidePwd.setImageResource(R.drawable.close_eye)
            } else {
                // Jika password sebelumnya ditampilkan, sembunyikan dengan karakter bintang
                edPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                ivShowHidePwd.setImageResource(R.drawable.show_eye)
            }
            // Set kursor ke akhir teks
            edPassword.setSelection(edPassword.text.length)
        }
    }

    private fun login(loginRequest: LoginRequest) {

        val call = ApiClient.apiService.login(loginRequest)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    val nik = loginResponse?.nik
                    if (token != null && token.isNotEmpty()) {
                        val sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE)
                        sharedPreferences.edit().putString("token", token).apply()
                        Toast.makeText(this@MainActivity, "Login Berhasil.", Toast.LENGTH_SHORT).show()
                        Log.e("Login", "Token : $token")

                        val intent = Intent(this@MainActivity, Dashboard::class.java)
                        intent.putExtra("nik", nik)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Login gagal. Silahkan coba lagi. 500", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Tambahkan penanganan ketika respon tidak berhasil (misalnya, kode respon menunjukkan login gagal karena password salah)
                    if (response.code() == 401) {
                        Toast.makeText(this@MainActivity, "Login gagal. Silahkan coba lagi. 401", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Login gagal. Android Error.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Handle failure
                t.printStackTrace()
            }
        })
    }
}
