package com.dicoding.mymediaplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.IOException
import java.lang.ref.WeakReference

class MediaService : Service(), MediaPlayerCallback {
    private var mMediaPlayer: MediaPlayer? = null
    private var isReady: Boolean = false

    companion object {
        const val ACTION_CREATE = "create"
        const val ACTION_DESTROY = "destroy"
        const val TAG = "MediaService"
        const val PLAY = 1
        const val STOP = 0
        const val CHANNEL_DEFAULT_IMPORTANCE = "Channel_Test"
        const val ONGOING_NOTIFICATION_ID = 1
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        if (action != null){
            when(action){
                //jika create init dijalankan
                ACTION_CREATE -> if(mMediaPlayer == null){
                    init()
                }
                //jika destroy maka service dihentikan
                ACTION_DESTROY -> if (mMediaPlayer?.isPlaying as Boolean){
                    stopSelf()
                }
                else -> {
                    init()
                }
            }
        }
        Log.d(TAG, "onStartCommand: ")
        return flags
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind: ")
        return mMessenger.binder
    }

    private fun init(){
        //kode untuk memperbarui mediaPlayer
        mMediaPlayer = MediaPlayer()
        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        mMediaPlayer?.setAudioAttributes(attribute)

        //kode untuk mengambil sound di didalam file Raw
        val afd = applicationContext.resources.openRawResourceFd(R.raw.guitar_background)
        try {
            //kode untuk memasukan detail informasi dari asset sound
            mMediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        }catch (e: IOException){
            e.printStackTrace()
        }

        //menjalankan mMediaPlayer
        mMediaPlayer?.setOnPreparedListener{
            isReady = true
            mMediaPlayer?.start()
            showNotif()
        }
        mMediaPlayer?.setOnErrorListener { mediaPlayer, i, i2 -> false }
    }

    override fun onPlay() {
        if (!isReady){
            //menyiapkan mediaplayer jika belum disiapkan atau diperbarui
            //kode ini bersifat asynchronus
            mMediaPlayer?.prepareAsync()
        }else{
            if (mMediaPlayer?.isPlaying as Boolean){
                //pause sound
                mMediaPlayer?.pause()
            }else{
                //start sound
                mMediaPlayer?.start()
                showNotif()
            }
        }
    }

    override fun onStop() {
        if (mMediaPlayer?.isPlaying as Boolean || isReady){
            //stop sound
            mMediaPlayer?.stop()
            isReady = false
            stopNotif()
        }
    }

    private val mMessenger = Messenger(IncomingHandler(this))

    //class penerima messengger yang dikirim ketika button di click
    internal class IncomingHandler(playerCallback: MediaPlayerCallback): Handler(Looper.getMainLooper()){
        private val mediaPlayerCallbackWeakReference: WeakReference<MediaPlayerCallback> = WeakReference(playerCallback)

        override fun handleMessage(msg: Message) {
            when(msg.what){
                //jika PLAY maka akan menjalankan method onPlay
                PLAY -> mediaPlayerCallbackWeakReference.get()?.onPlay()
                //jika STOP akan menjalankan method onStop
                STOP -> mediaPlayerCallbackWeakReference.get()?.onStop()
                else -> super.handleMessage(msg)
            }
        }
    }

    //membuat notifiaction
    private fun showNotif(){
        val notificationIntent = Intent(this, MainActivity::class.java)

        //FLAG BROUND TO FRONT berfungsi untuk memanggil activity yang sudah ada tanpa membuat activity baru dan menampilkannya.
        notificationIntent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        }else{
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
            .setContentTitle("TES1")
            .setContentText("TES2")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .setTicker("TES3")
            .build()

        createChannel(CHANNEL_DEFAULT_IMPORTANCE)

        //menjalankan foreground service
        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun createChannel(CHANNEL_ID: String) {
        val mNotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID, "Battery", NotificationManager.IMPORTANCE_DEFAULT)
            channel.setShowBadge(false)
            channel.setSound(null, null)
            mNotificationManager.createNotificationChannel(channel)
        }
    }

    //stop notification
    private fun stopNotif(){
        //stop foreground service
        stopForeground(false)
    }
}