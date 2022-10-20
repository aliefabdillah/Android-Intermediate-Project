package com.dicoding.myservice

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.myservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //membuat intent untuk menjalankan service
        val serviceIntent = Intent(this, MyBackgroundService::class.java)
        binding.btnStartBgService.setOnClickListener {
            //menjalankan service dengan menjalankan method onStartCommand
            startService(serviceIntent)
        }
        binding.btnStopBgService.setOnClickListener {
            //untuk memberhentikan service secara langsugn dari luar kelas service
            stopService(serviceIntent)
        }

        //inisialiasi intent untuk foreground service
        val foregroundService = Intent(this, MyForegroundService::class.java)
        binding.btnStartForegroundService.setOnClickListener {
            //menjalankan foreground service
            if (Build.VERSION.SDK_INT >= 26){
                startForegroundService(foregroundService)
            }else{
                startService(foregroundService)
            }
        }
        binding.btnStopForegroundService.setOnClickListener {
            //memberhentikan foregroundservice
            stopService(foregroundService)
        }
    }
}