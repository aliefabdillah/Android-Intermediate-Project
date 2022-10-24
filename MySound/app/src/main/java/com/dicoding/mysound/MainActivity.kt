package com.dicoding.mysound

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var sp: SoundPool
    private var soundId: Int = 0
    private var spLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //inisialiasi soundpool
        sp = SoundPool.Builder()
            .setMaxStreams(10)      //nilai 10 berarti jumlah streams secara simultan yang dapat diputar secara bersamaan
            .build()

        //soundpool hany bisa memutar sound yang di load secara sempurna sehingga perlu dilakukan pengecekan
        sp.setOnLoadCompleteListener { _, _, status ->
            if (status == 0){
                spLoaded = true
            }else {
                Toast.makeText(this, "Gagal Load", Toast.LENGTH_SHORT).show()
            }
        }

        //load sound, setelah load selesai id dimasukan ke dalam variabel soundID
        soundId = sp.load(this, R.raw.clinking_glasses, 1)

        val btnSound = findViewById<Button>(R.id.btn_sound_pool)

        btnSound.setOnClickListener {
            if (spLoaded){
                //menjalankan sound
                sp.play(soundId, 1f, 1f, 0, 0, 1f)
                /*
                Penjelsan parameter
                1. Parameter soundID merupakan id dari audio yang Anda muat.
                2. LeftVolume dan RightVolume merupakan parameter float dari besar kecilnya volume yang range-nya dimulai dari 0.0 - 1.0.
                3. Priority merupakan urutan prioritas. Semakin besar nilai priority, semakin tinggi prioritas audio itu untuk dijalankan.
                4. Paremeter loop digunakan untuk mengulang audio ketika telah selesai dimainkan. Nilai -1 menunjukkan bahwa audio akan diulang-ulang tanpa berhenti. Nilai 0 menunjukkan audio akan dimainkan sekali. Nilai 1 menunjukkan audio akan dimainkan sebanyak 2 kali.
                5. Parameter rate mempunyai range dari 0.5 - 2.0. Rate 1.0 akan memainkan audio secara normal, 0.5 akan memainkan audio dengan kecepatan setengah, dan 2.0 akan memainkan audio 2 kali lipat lebih cepat.
                * */
            }
        }
    }
}