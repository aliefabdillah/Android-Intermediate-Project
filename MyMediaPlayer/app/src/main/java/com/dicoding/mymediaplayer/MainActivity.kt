package com.dicoding.mymediaplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private var mService: Messenger? = null
    private lateinit var mBoundServiceIntent: Intent
    private var mServiceBound = false

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
            mServiceBound = false
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mService = Messenger(service)
            mServiceBound = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPlay = findViewById<Button>(R.id.btn_play)
        val btnStop = findViewById<Button>(R.id.btn_stop)

        btnPlay.setOnClickListener {
            //action button mengirimkan sebuah message ke mediaService
            if (mServiceBound){
                try {
                    mService?.send(Message.obtain(null, MediaService.PLAY, 0, 0))
                }catch (e: RemoteException){
                    e.printStackTrace()
                }
            }
        }

        btnStop.setOnClickListener {
            //action button mengirimkan sebuah message ke mediaService
            if (mServiceBound){
                try {
                    mService?.send(Message.obtain(null, MediaService.STOP, 0, 0))
                }catch (e: RemoteException){
                    e.printStackTrace()
                }
            }
        }

        mBoundServiceIntent = Intent(this, MediaService::class.java)

        //action intent yang dijalankan adalah create MediaService
        mBoundServiceIntent.action = MediaService.ACTION_CREATE

        //fungsi startservice digunakan untuk membuat dan menghancurkan kelas service,
        startService(mBoundServiceIntent)
        //bindservice digunakan untuk mengaitkan kelas service dengan mainActivity
        bindService(mBoundServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }


    //on Destroy digunakna ketika activity dihancurkan maka service akan dihancurkan juga
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        unbindService(mServiceConnection)
        //action intent yang dijalankan adalah destroy MediaService
        mBoundServiceIntent.action = MediaService.ACTION_DESTROY

        startService(mBoundServiceIntent)
    }
}