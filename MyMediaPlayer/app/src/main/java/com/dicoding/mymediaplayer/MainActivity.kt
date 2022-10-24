package com.dicoding.mymediaplayer

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var mMediaPlayer: MediaPlayer? = null
    private var isReady: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPlay = findViewById<Button>(R.id.btn_play)
        val btnStop = findViewById<Button>(R.id.btn_stop)

        btnPlay.setOnClickListener {
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
                }
            }
        }

        btnStop.setOnClickListener {
            if (mMediaPlayer?.isPlaying as Boolean || isReady){
                //stop sound
                mMediaPlayer?.stop()
                isReady = false
            }
        }

        init()
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
        }
        mMediaPlayer?.setOnErrorListener { mediaPlayer, i, i2 -> false }
    }
}