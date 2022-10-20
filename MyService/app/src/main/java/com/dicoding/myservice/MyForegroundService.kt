package com.dicoding.myservice

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class MyForegroundService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "dicoding_channel"
        internal val TAG = MyForegroundService::class.java.simpleName
    }

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    //method membuat notification untuk menandakan service yangsedangberjalan
    private fun buildNotification(): Notification{
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingFlags: Int = if (Build.VERSION.SDK_INT >= 23){
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        }else{
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingFlags)
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Saat ini foreground service sedang berjalan.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_NAME
            notificationBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }

        return notificationBuilder.build()

    }

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not Yet Implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notification = buildNotification()
        //menajalankan foreground service
        startForeground(NOTIFICATION_ID, notification)

        Log.d(MyBackgroundService.TAG, "Service dijalankan...")
        serviceScope.launch {
            for (i in 1..50){
                delay(1000)
                Log.d(MyBackgroundService.TAG, "Do Something $i")
            }
            //stopSelf digunakan untuk memberhentikan service di dalam kelas service itu sendiri
            stopForeground(true)
            stopSelf()
            Log.d(MyBackgroundService.TAG, "Service dihentikan")
        }
        //START_STICKY menandakan bila service tersebut dimatikan oleh sistem Android karena kekurangan memori,
        // ia akan diciptakan kembali jika sudah ada memori yang bisa digunakan. Sehingga, metode onStartCommand() juga akan kembali dijalankan.
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        Log.d(MyBackgroundService.TAG, "onDestroy: Service Dihentikan")
    }
}