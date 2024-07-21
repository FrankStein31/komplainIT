package com.frankie.komplain

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.frankie.komplain.services.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity(){
    private var isPasswordVisible = false
    private lateinit var edPasswordReg: EditText
    private lateinit var edConfirmPassword: EditText
    private lateinit var edNikReg: EditText
    private lateinit var edNamaReg: EditText
    private lateinit var edEmailReg: EditText
    private lateinit var edHpReg: EditText
    private lateinit var logoregis: ImageView
    private lateinit var txLoginReg: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        getSupportActionBar()?.hide()

        val btnRegister: Button = findViewById(R.id.btnRegis)
        edPasswordReg = findViewById(R.id.edPasswordReg)
        edConfirmPassword = findViewById(R.id.edConfirmPassword)
        edNikReg = findViewById(R.id.edNikReg)
        edNamaReg = findViewById(R.id.edNamaReg)
        edEmailReg = findViewById(R.id.edEmailReg)
        edHpReg = findViewById(R.id.edHpReg)
        logoregis = findViewById(R.id.logoregis)
        txLoginReg = findViewById(R.id.txLoginReg)
        logoregis.setImageResource(R.drawable.logoo)

        txLoginReg.setOnClickListener{
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
        }

        btnRegister.setOnClickListener {
            val nik = edNikReg.text.toString()
            val name = edNamaReg.text.toString()
            val email = edEmailReg.text.toString()
            val phone = edHpReg.text.toString()
            val password = edPasswordReg.text.toString()
            val confirmPassword = edConfirmPassword.text.toString()

            if (nik.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this@RegisterActivity, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this@RegisterActivity, "Password and Confirm Password do not match.", Toast.LENGTH_SHORT).show()
            } else {
                val registerRequest = RegisterRequest(nik, name, email, phone, password, confirmPassword)
                register(registerRequest)
            }
        }

        //fungsi show dan hide password
        val ivShowHidePwd: ImageView = findViewById(R.id.iv_show_hide_pwd)
        ivShowHidePwd.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                // Jika password sebelumnya disembunyikan, tampilkan sebagai teks biasa
                edPasswordReg.transformationMethod = null
                ivShowHidePwd.setImageResource(R.drawable.close_eye)
            } else {
                // Jika password sebelumnya ditampilkan, sembunyikan dengan karakter bintang
                edPasswordReg.transformationMethod = PasswordTransformationMethod.getInstance()
                ivShowHidePwd.setImageResource(R.drawable.show_eye)
            }
            // Set kursor ke akhir teks
            edPasswordReg.setSelection(edPasswordReg.text.length)
        }

        //fungsi show dan hide password
        val ivShowHidePwd2: ImageView = findViewById(R.id.iv_show_hide_pwd2)
        ivShowHidePwd2.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                // Jika password sebelumnya disembunyikan, tampilkan sebagai teks biasa
                edConfirmPassword.transformationMethod = null
                ivShowHidePwd2.setImageResource(R.drawable.close_eye)
            } else {
                // Jika password sebelumnya ditampilkan, sembunyikan dengan karakter bintang
                edConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                ivShowHidePwd2.setImageResource(R.drawable.show_eye)
            }
            // Set kursor ke akhir teks
            edConfirmPassword.setSelection(edConfirmPassword.text.length)
        }
    }

    private fun register(registerRequest: RegisterRequest) {
        val call = ApiClient.apiService.register(registerRequest)

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null) {
                        Toast.makeText(this@RegisterActivity, registerResponse.message, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    Log.e("RegisterActivity", "Registrasi gagal. Tidak dapat mengirim permintaan.")
                    // Handle unsuccessful response
                    Toast.makeText(this@RegisterActivity, "Registrasi gagal. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                // Handle failure
                t.printStackTrace()
                Log.e("RegisterActivity", "Registrasi gagal. Terjadi kesalahan jaringan.")
                Toast.makeText(this@RegisterActivity, "Registrasi gagal. Terjadi kesalahan jaringan.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}