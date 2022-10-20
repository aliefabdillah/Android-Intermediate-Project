package com.dicoding.myservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*

class MyBackgroundService : Service() {

    companion object {
        internal val TAG = MyBackgroundService::class.java.simpleName
    }

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main +  serviceJob)

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not Yet Implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service dijalankan...")
        serviceScope.launch {
            for (i in 1..50){
                delay(1000)
                Log.d(TAG, "Do Something $i")
            }
            //stopSelf digunakan untuk memberhentikan service di dalam kelas service itu sendiri
            stopSelf()
            Log.d(TAG, "Service dihentikan")
        }
        //START_STICKY menandakan bila service tersebut dimatikan oleh sistem Android karena kekurangan memori,
        // ia akan diciptakan kembali jika sudah ada memori yang bisa digunakan. Sehingga, metode onStartCommand() juga akan kembali dijalankan.
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        Log.d(TAG, "onDestroy: Service Dihentikan")
    }
}