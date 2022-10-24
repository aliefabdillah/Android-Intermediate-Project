package com.dicoding.myplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dicoding.myplayer.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.Util

class MainActivity : AppCompatActivity() {
    companion object {
        const val URL_VIDEO = "https://github.com/dicodingacademy/assets/releases/download/release-video/VideoDicoding.mp4"
        const val URL_AUDIO = "https://github.com/dicodingacademy/assets/raw/main/android_intermediate_academy/bensound_ukulele.mp3"
    }

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE){
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
    }

    /* Menyeseuaikan Exoplayer dengan Activity*/
    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23){
            initializePlayer()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        if (Util.SDK_INT <= 23 && player == null){
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    //method mengubah tampilan menjadi fullscreen
    private fun hideSystemUI(){
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, viewBinding.videoView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private var player: ExoPlayer? = null
    private fun initializePlayer() {
        //mengambil asset
        val mediaItem = MediaItem.fromUri(URL_VIDEO)
        val anotherMediaItem = MediaItem.fromUri(URL_AUDIO)

        //membuat exoplayer
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            //menerapakn pada layout
            viewBinding.videoView.player = exoPlayer
            //memasukan asset ke dalam exoplyaer
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.addMediaItem(anotherMediaItem)
            exoPlayer.prepare()
        }
    }

    //method melepaskan exoplayer
    private fun releasePlayer(){
        player?.release()
        player = null
    }
}