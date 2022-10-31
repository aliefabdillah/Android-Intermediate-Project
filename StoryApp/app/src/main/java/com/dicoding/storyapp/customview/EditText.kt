package com.dicoding.storyapp.customview

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
import com.dicoding.storyapp.R

class EditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var endButton: Drawable
    private lateinit var startIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init(){
        endButton = ContextCompat.getDrawable(context, R.drawable.ic_clear) as Drawable

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    showClearButton()
                    if (s.length < 6){
                    }
                } else hideClearButton()
            }

            override fun afterTextChanged(p0: Editable?) {
                //
            }

        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        startIcon = if (hint == context.getString(R.string.name_hint)){
            ContextCompat.getDrawable(context, R.drawable.state_icon_person) as Drawable
        }else{
            ContextCompat.getDrawable(context, R.drawable.state_icon_email) as Drawable
        }
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    override fun onTouch(v: View?, motionEvent: MotionEvent): Boolean {

        if (compoundDrawables[2] != null){
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL){
                clearButtonEnd = (endButton.intrinsicWidth + paddingStart).toFloat()
                //dilakukan pengecekan apabila area yang ditekan adalah tempat tombol clear berada maka variabel isClearButtonClicked akan bernilai true
                when {
                    motionEvent.x < clearButtonEnd -> isClearButtonClicked = true
                }
            }else{
                clearButtonStart = (width - paddingEnd - endButton.intrinsicWidth).toFloat()
                when {
                    motionEvent.x > clearButtonStart -> isClearButtonClicked = true
                }
            }

            if (isClearButtonClicked){
                when(motionEvent.action){
                    //ketika tombol ditekan makan tombol clear akan tetap tampil
                    MotionEvent.ACTION_DOWN -> {
                        endButton = ContextCompat.getDrawable(context, R.drawable.ic_clear) as Drawable
//                        startIcon = ContextCompat.getDrawable(context, R.drawable.state_icon_email) as Drawable
                        showClearButton()
                        return true
                    }
                    //ketika tombol dilepas maka tombol clear akan hilang
                    MotionEvent.ACTION_UP -> {
                        endButton = ContextCompat.getDrawable(context, R.drawable.ic_clear) as Drawable
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

    private fun showClearButton(){
        setButtonDrawables(startOfTheText = startIcon, endOfTheText = endButton)
    }

    private fun hideClearButton(){
        //pada method ini kita tidak mengisi apa" karena ingin menghilangkan gambar
        setButtonDrawables(startOfTheText = startIcon)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null,
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,topOfTheText,endOfTheText,bottomOfTheText
        )
    }
}