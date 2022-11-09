package com.dicoding.mygeofence

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        //cek apakah action sudah sesuai
        if (intent.action == ACTION_GEOFENCE_EVENT){
            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            //mendapatkan data geofence
            if (geofencingEvent?.hasError() == true){
                val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                Log.e(TAG, errorMessage)
                return
            }

            val geofenceTransition = geofencingEvent?.geofenceTransition

            //membaca geofence transisi
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){

                //membuat pesan notifikasi berdasarkan transisi geofence
                val geofenceTransitionString =
                    when(geofenceTransition){
                        Geofence.GEOFENCE_TRANSITION_ENTER -> "Anda telah memasuki area"
                        Geofence.GEOFENCE_TRANSITION_DWELL -> "Anda telah di dalam area"
                        else -> "Invalid transition type"
                    }

                val triggeringGeofences = geofencingEvent.triggeringGeofences
                val requestId = triggeringGeofences?.get(0)?.requestId          //mengambil request id

                val geofenceTransitionDetails = "$geofenceTransitionString $requestId"
                Log.i(TAG, geofenceTransitionDetails)

                //menampilkan notifikasi
                sendNotifications(context, geofenceTransitionDetails)
            }else{
                //jika terdapat error
                val errorMessage = "Invalid transition type: $geofenceTransition"
                Log.e(TAG, errorMessage)
                //menampilkan notifikasi error
                sendNotifications(context, errorMessage)
            }
        }
    }

    private fun sendNotifications(context: Context, text: String) {
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(text)
            .setContentText("Anda sudah bisa absen sekarang :) ")
            .setSmallIcon(R.drawable.ic_notifications)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()
        mNotificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "1"
        private const val CHANNEL_NAME = "Geofence Channel"
        private const val NOTIFICATION_ID = 1
        private const val TAG = "GeofenceBroadcast"
        const val ACTION_GEOFENCE_EVENT = "GeofenceEvent"
    }
}