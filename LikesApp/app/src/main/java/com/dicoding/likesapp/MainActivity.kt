package com.dicoding.likesapp

import android.graphics.*
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.dicoding.likesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mBitmap = Bitmap.createBitmap(1000,1000, Bitmap.Config.ARGB_8888)
    private val mCanvas = Canvas(mBitmap)
    private val mPaint = Paint()

    private val halfOfWidth = (mBitmap.width/2).toFloat()
    private val halfOfHeight = (mBitmap.height/2).toFloat()

    private val left = 150F
    private val top = 250F
    private val right = mBitmap.width - left
    private val bottom = mBitmap.height.toFloat() - 50F

    private var message = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgView.setImageBitmap(mBitmap)
        message = getString(R.string.question)
        showText()

        binding.like.setOnClickListener {
            showEars()
            showFace()
            showEye()
            showNose()
            showMouth(true)
            showHair()

        }

        binding.dislike.setOnClickListener {
            showEars()          //agar wajah dapat menimpa telinga maka showFace harus diatas dibawah showEars
            showFace()
            showEye()
            showNose()
            showMouth(false)
            showHair()
        }
    }

    private fun showFace(){
        val face = RectF(left, top, right, bottom)

        /*DrawARc digunakan untuk menggambar sebuah oval atau lonjong
        * tetapi kita harusbisa mengatur di bagian mana yang akan di gambar*/
        mPaint.color = ResourcesCompat.getColor(resources, R.color.yellow_left_skin, null)
        mCanvas.drawArc(face, 90F, 180F, false, mPaint)
        /*5 parameeter pada draw Arc
        -Face yang digunakan untuk menentukan posisi dari object oval atau lonjong.
        -Sudut pertama atau sebagai titik pertama sebuah object akan digambar.
        -Total sudut yang akan digambar pada object tersebut.
        -Menggambar object dengan titik pusat atau intinya.
        -Paint yang digunakan untuk mewarnai object.
        * */

        mPaint.color = ResourcesCompat.getColor(resources, R.color.yellow_right_skin, null)
        mCanvas.drawArc(face, 270F, 180F, false, mPaint)
    }

    private fun showEye(){
        //mata bagian luar
        mPaint.color = ResourcesCompat.getColor(resources, R.color.black, null)
        //draw circle dipakai untuk menggambar lingkaran
        mCanvas.drawCircle(halfOfWidth - 100F, halfOfHeight - 10F, 50F, mPaint) //sebelah kiri
        mCanvas.drawCircle(halfOfWidth + 100F, halfOfHeight - 10F, 50F, mPaint) //kanan

        //mata bagian dalam
        mPaint.color = ResourcesCompat.getColor(resources, R.color.white, null)
        mCanvas.drawCircle(halfOfWidth - 120F, halfOfHeight - 20F, 15F, mPaint) //kiri
        mCanvas.drawCircle(halfOfWidth + 80F, halfOfHeight - 20F, 15F, mPaint)
    }

    private fun showMouth(isHappy: Boolean){
        when(isHappy){
            true -> {
                mPaint.color = ResourcesCompat.getColor(resources, R.color.black, null)
                val lip = RectF(halfOfWidth - 200F, halfOfHeight - 100F, halfOfWidth + 200F, halfOfHeight + 400F)
                mCanvas.drawArc(lip, 25F, 130F, false, mPaint)

                mPaint.color = ResourcesCompat.getColor(resources, R.color.white, null)
                val mouth = RectF(halfOfWidth - 180F, halfOfHeight, halfOfWidth + 180F, halfOfHeight + 380F)
                mCanvas.drawArc(mouth, 25F, 130F, false, mPaint)
            }
            false -> {
                mPaint.color = ResourcesCompat.getColor(resources, R.color.black, null)
                val lip = RectF(halfOfWidth - 200F, halfOfHeight + 250F, halfOfWidth + 200F, halfOfHeight + 350F)
                mCanvas.drawArc(lip, 0F, -180F, false, mPaint)

                mPaint.color = ResourcesCompat.getColor(resources, R.color.white, null)
                val mouth = RectF(halfOfWidth - 180F, halfOfHeight + 250F, halfOfWidth + 180F, halfOfHeight + 330F)
                mCanvas.drawArc(mouth, 0F, -180F, false, mPaint)
            }
        }
    }

    //method menampilkan text
    private fun showText(){
        val mPaintText = Paint(Paint.FAKE_BOLD_TEXT_FLAG).apply {
            textSize = 50F
            color = ResourcesCompat.getColor(resources, R.color.black, null)
        }

        //merupakan kontainer berbentuk kotak untuk menampung text
        val mBounds = Rect()
        mPaintText.getTextBounds(message, 0, message.length, mBounds)

        val x: Float = halfOfWidth - mBounds.centerX()
        val y = 50F
        mCanvas.drawText(message, x, y , mPaintText)
    }

    private fun showNose(){
        mPaint.color = ResourcesCompat.getColor(resources, R.color.black, null)
        mCanvas.drawCircle(halfOfWidth - 40F, halfOfHeight + 140F, 15F, mPaint)
        mCanvas.drawCircle(halfOfWidth + 40F, halfOfHeight + 140F, 15F, mPaint)
    }

    private fun showEars(){
        //telinga kiri
        mPaint.color = ResourcesCompat.getColor(resources, R.color.brown_left_hair, null)
        mCanvas.drawCircle(halfOfWidth - 300F, halfOfHeight - 100F, 100F, mPaint)

        //telinga kanan
        mPaint.color = ResourcesCompat.getColor(resources, R.color.brown_right_hair, null)
        mCanvas.drawCircle(halfOfWidth + 300F, halfOfHeight - 100F, 100F, mPaint)

        //telinga dalam
        mPaint.color = ResourcesCompat.getColor(resources, R.color.red_ear, null)
        mCanvas.drawCircle(halfOfWidth - 300F, halfOfHeight - 100F, 60F, mPaint)
        mCanvas.drawCircle(halfOfWidth + 300F, halfOfHeight - 100F, 60F, mPaint)
    }

    //method menampilkan rambut dengan clipping pada object shape
    private fun showHair(){
        //Menyimpan pengaturan canvas saat ini.
        mCanvas.save()

        /*
        * Path digunakan untuk menggambar sebuah garis yang saling berkaitan, hingga membentuk sebuah object.
        *Anda bisa menggambar sebuah object path ke dalam Canvas menggunakan drawPath. Tentunya, ketika
        * memanggil fungsi drawPath, Anda perlu menentukan warna pada object tersebut menggunakan paint.  */
        val path = Path()

        path.addCircle(halfOfWidth - 100F, halfOfHeight - 10F, 150F, Path.Direction.CCW)
        path.addCircle(halfOfWidth + 100F, halfOfHeight - 10F, 150F, Path.Direction.CCW)

        val mouth = RectF(halfOfWidth - 250F, halfOfHeight, halfOfWidth + 250F, halfOfHeight + 500F)
        path.addOval(mouth, Path.Direction.CCW)

        //clip berdasarkan version device
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            /*
            * Parameter Region.Op digunakan untuk menentukan bagaimana object clip dimodifikasi
            * Difference. Ia memiliki kemampuan untuk memotong dan menyisakan bagian yang tidak beririsan*/
            mCanvas.clipPath(path, Region.Op.DIFFERENCE)
        }else {
            mCanvas.clipOutPath(path)
        }

        //Object yang tampil akan terpotong berdasarkan path yang telah diatur.
        val face = RectF(left, top, right, bottom)
        mPaint.color = ResourcesCompat.getColor(resources, R.color.brown_left_hair, null)
        mCanvas.drawArc(face, 90F, 180F, false, mPaint)

        mPaint.color = ResourcesCompat.getColor(resources, R.color.brown_right_hair, null)
        mCanvas.drawArc(face, 270F, 180F, false, mPaint)

        //restore untuk menyimpan dan mengembalikan pengaturan canvas yang ada sebelum di clip.
        mCanvas.restore()

    }
}