package com.dicoding.myfirebasechat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    //method ini berguna untuk penerima ketika notif dikirimkan dari server,
    //token ini berbeda setiap aplikasi dan akan berubah ketika cache aplikasi dibersihkan
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    //method ini dipanggil ketika ada push notif yang datagn
    /*
    Pesan tersebut akan diterima dalam bentuk RemoteMessage. RemoteMessage berisi berbagai macam data
    seperti id pengirim, data dari additional options serta data notifikasi seperti judul dan body.
    * */
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "From: ${message.from}")
        Log.d(TAG, "Message data payload: " + message.data)
        Log.d(TAG, "Message Notification Body: ${message.notification?.body}")

        sendNotification(message.notification?.title, message.notification?.body)
    }

    private fun sendNotification(title: String?, body: String?) {
        val contentIntent = Intent(applicationContext, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {
        private val TAG = MyFirebaseMessagingService::class.java.simpleName
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "Firebase Channel"
        private const val NOTIFICATION_CHANNEL_NAME = "Firebase Notification"
    }
}

/*
ffvwCK0_TBCgK6mA5kXZlE:APA91bHCZkUlOYgjVQzWiqLi3dLzEmlkh046rEM8nyUDXglwsVy6J0WulXMeVOuM6hNjkLI3V0d5K-Rgqr1VgoTb4K8xXDjD7UJXa3IgIJdStbTLltHwAg_UlqB8WXgdn8q7ld-YUB0_
* */