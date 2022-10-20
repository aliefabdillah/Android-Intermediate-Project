package com.dicoding.myservice

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.dicoding.myservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var boundStatus = false
    private lateinit var boundService: MyBoundService

    /*
    * connection merupakan sebuah ServiceConnection yang berfungsi sebagai callback dari kelas MyBoundService.*/
    private val connection = object : ServiceConnection {
        //ketika service terputus
        override fun onServiceDisconnected(name: ComponentName) {
            boundStatus = false
        }

        //ketika service terhubung
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val myBinder = service as MyBoundService.MyBinder
            boundService = myBinder.getService
            boundStatus = true
            getNumberFromService()
        }
    }

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

        //inisialiasi intent boundService
        val boundService = Intent(this, MyBoundService::class.java)
        binding.btnStartBoundService.setOnClickListener {
            //method untuk mengikat kelas myBoundService ke kelas MainActivity
            //BIND_AUTO_CREATE yang membuat sebuah service jika service tersebut belum aktif
            bindService(boundService, connection, BIND_AUTO_CREATE)
        }
        binding.btnStopBoundService.setOnClickListener {
            //method melepaskan ikatan service dengan activity dan memanggil method onDestroy di class service
            unbindService(connection)
        }
    }

    /*
    *Kode onStop() seperti yang dijelaskan di metode sebelumnya akan memanggil unbindService atau
    * melakukan pelepasan service dari Activity. Pemanggilan unbindService di dalam onStop ditujukan
    * untuk mencegah memory leaks dari bound services. */
    override fun onStop() {
        super.onStop()
        if (boundStatus){
            unbindService(connection)
            boundStatus = false
        }
    }

    private fun getNumberFromService() {
        boundService.numberLiveData.observe(this) { number ->
            binding.tvBoundServiceNumber.text = number.toString()
        }
    }
}