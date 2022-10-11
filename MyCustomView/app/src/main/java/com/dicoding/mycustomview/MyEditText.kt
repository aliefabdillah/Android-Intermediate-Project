package com.dicoding.mycustomview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat

class MyEditText: AppCompatEditText, View.OnTouchListener {

    private lateinit var clearButtonImage: Drawable

    constructor(context: Context): super(context){
        //fungsi init digunakan ketika ingin menambahkan aksi pada custom view
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Masukan Nama"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    /*
    * Kode pada method init berisi behaviour dari komponen custome view itu sendiri*/
    private fun init(){
        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24) as Drawable
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //do nothing
            }

            //method ini digunakan ketika teks pada editText berubah tombol clear akan tetap muncul
            //dan dihilangkan ketika editText Kosong
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()
            }

            override fun afterTextChanged(p0: Editable?) {
                //do nothing
            }

        })
    }

    private fun showClearButton(){
        //pada method ini kita ingin menampilkan gambar pada sebelah kanan komponen
        setButtonDrawables(endOfTheText = clearButtonImage)
    }

    private fun hideClearButton(){
        //pada method ini kita tidak mengisi apa" karena ingin menghilangkan gambar
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null,
    ){
        //method ini digunakan untuk menampilkan gambar pada editText
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,topOfTheText,endOfTheText,bottomOfTheText
        )
    }

    /*
    * Method ini akan digunakan ketika komponen Edit Text ditekan*/
    override fun onTouch(v: View?, motionEvent: MotionEvent): Boolean {
        if (compoundDrawables[2] != null){
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false

            /*disini juga terdapat pengecekan jenis handphone yangdigunakan
            * RTL artinya format handphone adalah dari kanan ke kiri seperti bahasa arab
            * Untuk settingan biasa dapat menggunakan LTR / Left To Right*/
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL){
                clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                //dilakukan pengecekan apabila area yang ditekan adalah tempat tombol clear berada maka variabel isClearButtonClicked akan bernilai true
                when {
                    motionEvent.x < clearButtonEnd -> isClearButtonClicked = true
                }
            }else{
                clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                when {
                    motionEvent.x > clearButtonStart -> isClearButtonClicked = true
                }
            }

            if (isClearButtonClicked){
                when(motionEvent.action){
                    //ketika tombol ditekan makan tombol clear akan tetap tampil
                    MotionEvent.ACTION_DOWN -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24) as Drawable
                        showClearButton()
                        return true
                    }
                    //ketika tombol dilepas maka tombol clear akan hilang
                    MotionEvent.ACTION_UP -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearButton()
                        return true
                    }
                    else -> return false
                }
            }else return false
        }
        return false
    }
}