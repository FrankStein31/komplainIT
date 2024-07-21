package com.frankie.komplain

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.frankie.komplain.databinding.FirstBinding

class firstActivity : AppCompatActivity(){
    private val delayMillis: Long = 3000 // Waktu tunda dalam milidetik
    lateinit var bin: FirstBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bin = FirstBinding.inflate(layoutInflater)
        setContentView(bin.root)
        getSupportActionBar()?.hide()

        // Inisialisasi animasi dari animasi XML
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // Terapkan animasi ke ImageView (logo)
        bin.firstlogo.startAnimation(animation)

        // Set efek transisi saat perpindahan ke welcomeActivity
        val slideIn = R.anim.slide_in
        val slideOut = R.anim.slide_out
        val options = ActivityOptions.makeCustomAnimation(this, slideIn, slideOut)

        // Setelah penundaan, arahkan ke welcomeActivity
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent, options.toBundle())
            finish() // Menutup firstActivity
        }, delayMillis)
    }
}
