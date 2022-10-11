package com.dicoding.mycustomview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat

class MyButton : AppCompatButton {

    private lateinit var enableBg: Drawable
    private lateinit var disableBg: Drawable
    private var txtColor: Int = 0

    /*
    * Ketika membuat custome view perlu menambahkan constructor, kenapa harus 3? karena
    * setiap device memiliki kebutuhan berbeda-beda*/
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        init()
    }

    /*
    * untuk aksi pada component perlu di taruh di dalam  konstruktor, akan tetapi pada perubahan bentuk
    * harus menaruh kode pada onDraw*/
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //mengubah background dari button
        background = if (isEnabled) enableBg else disableBg

        //mengubah warna text
        setTextColor(txtColor)

        //mengubah ukuran text
        textSize = 12f

        //menjadikan text menjadi di tengah
        gravity = Gravity.CENTER

        //mengubah text sesuai kondisi isEnabled
        text = if (isEnabled) "Submit" else "Isi Dulu"
    }

    private fun init(){
        //pemanggilan resource harus dilakukan di constructor karena jika di panggil di onDraw maka resource akan dipanggil terus menerus
        txtColor = ContextCompat.getColor(context, android.R.color.background_light)
        enableBg = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
        disableBg = ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable
    }
}