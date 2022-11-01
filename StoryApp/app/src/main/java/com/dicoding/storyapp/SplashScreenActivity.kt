package com.dicoding.storyapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.databinding.ActivitySplashScreenBinding
import com.dicoding.storyapp.models.MainViewModel
import com.dicoding.storyapp.models.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

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