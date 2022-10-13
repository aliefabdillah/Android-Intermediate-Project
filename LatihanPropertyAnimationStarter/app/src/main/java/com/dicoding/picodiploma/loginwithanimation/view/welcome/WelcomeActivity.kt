package com.dicoding.picodiploma.loginwithanimation.view.welcome

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityWelcomeBinding
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginActivity
import com.dicoding.picodiploma.loginwithanimation.view.signup.SignupActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun playAnimation(){
        /*
        * kode ini berfungsi agar gambar memiliki animasi bergeser dari posisi x -30f sampai 30f atau
        * bergerak sebanyak 60f.
        * */
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000                             //waktu durasi animasi
            repeatCount = ObjectAnimator.INFINITE       //kode agar animasi terus berjalan
            repeatMode = ObjectAnimator.REVERSE         //kode agar view dapat kembali ke titik semula
        }.start()

        //animasi dibawah gambar
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 1f).setDuration(500)

        //mengatur animasi pada tombol login dan singup agar berjalan bersamaan
        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        //mengatur animasi title, desc dan gabungan login dan signup berjalan berurutan
        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }

        //car alain
        /*
        AnimatorSet().apply {
           play(login).with(signup)
           play(login).after(desc)
           play(title).before(desc)
           start()
        }
        * */
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}