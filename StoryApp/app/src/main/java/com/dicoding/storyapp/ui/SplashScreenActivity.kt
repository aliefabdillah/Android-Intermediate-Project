package com.dicoding.storyapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.databinding.ActivitySplashScreenBinding
import com.dicoding.storyapp.models.MainViewModel
import com.dicoding.storyapp.models.ViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private val viewModel: MainViewModel by viewModels { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.storyAppLogo.alpha = 0f
        binding.storyAppLogo.animate().setDuration(3000).alpha(1f).withEndAction{
            viewModel.getUser().observe(this){ user ->
                if (user.isLogin){
                    startActivity(Intent(this, ListStoryActivity::class.java))
                }else{
                    val i = Intent(this@SplashScreenActivity, SignInActivity::class.java)
                    startActivity(i)
                }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }
    }
}