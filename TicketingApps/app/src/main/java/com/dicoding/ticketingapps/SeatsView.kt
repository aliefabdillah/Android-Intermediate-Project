package com.dicoding.ticketingapps

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat

class SeatsView : View {
    private val seats: ArrayList<Seat> = arrayListOf(
        Seat(id = 1, name = "A1", isBooked = false),
        Seat(id = 2, name = "A2", isBooked = false),
        Seat(id = 3, name = "B1", isBooked = false),
        Seat(id = 4, name = "A4", isBooked = false),
        Seat(id = 5, name = "C1", isBooked = false),
        Seat(id = 6, name = "C2", isBooked = false),
        Seat(id = 7, name = "D1", isBooked = false),
        Seat(id = 8, name = "D2", isBooked = false),
    )

    var seat: Seat? = null
    private val backgroundPaint = Paint()
    private val armrestPain = Paint()
    private val bottomSeatPaint =  Paint()
    private val mBounds = Rect()
    private val numberSeatPaint = Paint(Paint.FAKE_BOLD_TEXT_FLAG)
    private val titlePaint = Paint(Paint.FAKE_BOLD_TEXT_FLAG)

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //method on draw digunakan untk merender komponen
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        /*Krena terdapat 8 komponen yang sama maka dapat menggunakan perulangan */
        for (seat in seats){
            drawSeat(canvas, seat)
        }

        //menambahkan text di atas canvas
        val text = "Silahkan Pilih Kursi"
        titlePaint.apply {
            textSize = 50F
        }
        canvas?.drawText(text, (width / 2) - 220F, 150F, titlePaint)
    }

    /* Method ini digunakan untuk menentukan posisi x dan y dari komponen
    * Jadi dalam proses onMeasure, kita menentukan beberapa titik x dan y dari masing-masing kursi.
    * Setelah menemukan titiknya, kita dapat mulai menggambar masing-masing kursi.*/
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)

        val halfOfHeight = height / 2
        val halfOfWidth = width / 2
        var value = -600f

        for (i in 0..7){
            if (i.mod(2) == 0){
                seats[i].apply {
                    x = halfOfWidth - 300F
                    y = halfOfHeight + value
                }
            }else {
                seats[i].apply {
                    x = halfOfWidth + 100F
                    y = halfOfHeight + value
                }
                value += 300F
            }
        }
    }

    /*
    * method ini dipakai untuk mengatur gestur pengguna ketika menggunakan aplikasi, seperti ketika
    * menyentuhkan jarinya ke dalam layar maupun ketika mengangkatnya.
    *
    * Seperti yang telah dijelaskan dalam latihan, kita menggunakan onTouch agar bisa menyeleksi
    * bagian yang disentuh, apakah berada dalam jangkauan kursi atau tidak. Sebab, ketika menggunakan
    * onClick, baik ketika pengguna menyentuh atau tidak, Custom View akan dianggap sudah tertekan/terklik.
    * Selain itu, Anda juga perlu mengetahui bahwa onClick tidak menyediakan lokasi mana yang tersentuh (x dan y).
    * Oleh karena itu, sebaiknya gunakanlah onTouchEvent untuk men-trigger Custom View ketika berada dalam posisi yang telah ditentukan.*/
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val halfOfHeight = height / 2
        val halfOfWidth = width / 2

        val widthColumnOne = (halfOfWidth - 300F)..(halfOfWidth - 100F)
        val widthColumnTwo = (halfOfWidth + 100F)..(halfOfWidth + 300F)

        val heightRowOne = (halfOfHeight - 600F)..(halfOfHeight - 400F)
        val heightRowTwo = (halfOfHeight - 300F)..(halfOfHeight - 100F)
        val heightRowTree = (halfOfHeight + 0F)..(halfOfHeight + 200F)
        val heightRowFour =(halfOfHeight + 300F)..(halfOfHeight + 500F)

        if (event?.action == ACTION_DOWN){
            when {
                event.x in widthColumnOne && event.y in heightRowOne -> booking(0)
                event.x in widthColumnTwo && event.y in heightRowOne -> booking(1)
                event.x in widthColumnOne && event.y in heightRowTwo -> booking(2)
                event.x in widthColumnTwo && event.y in heightRowTwo -> booking(3)
                event.x in widthColumnOne && event.y in heightRowTree -> booking(4)
                event.x in widthColumnTwo && event.y in heightRowTree -> booking(5)
                event.x in widthColumnOne && event.y in heightRowFour -> booking(6)
                event.x in widthColumnTwo && event.y in heightRowFour -> booking(7)
            }
        }

        return true
    }

    private fun booking(pos: Int) {
        for (seat in seats){
            seat.isBooked = false
        }

        seats[pos].apply {
            seat = this
            isBooked = true
        }

        //fungsi invalidate digunakan untuk merefresh method onDraw dalam SeatsView
        //maka poisisi kursi yang telah dibooking usdah disimpan pada array seats
        invalidate()
    }

    /*oada method ini digunakan untuk menggambar bagian" kursi*/
    private fun drawSeat(canvas: Canvas?, seat: Seat) {
        //mengatur warna ketika sudah dibook
        if (seat.isBooked){
            backgroundPaint.color = ResourcesCompat.getColor(resources, R.color.grey_200, null)
            armrestPain.color = ResourcesCompat.getColor(resources, R.color.grey_200, null)
            bottomSeatPaint.color = ResourcesCompat.getColor(resources, R.color.grey_200, null)
            numberSeatPaint.color = ResourcesCompat.getColor(resources, R.color.black, null)
        }else{
            backgroundPaint.color = ResourcesCompat.getColor(resources, R.color.blue_500, null)
            armrestPain.color = ResourcesCompat.getColor(resources, R.color.blue_700, null)
            bottomSeatPaint.color = ResourcesCompat.getColor(resources, R.color.blue_200, null)
            numberSeatPaint.color = ResourcesCompat.getColor(resources, R.color.grey_200, null)
        }

        //menyimpan state
        canvas?.save()

        //background
        canvas?.translate(seat.x as Float, seat.y as Float)

        val backgroundPath = Path()
        backgroundPath.addRect(0F, 0F, 200F, 200F, Path.Direction.CCW)
        backgroundPath.addCircle(100F, 50F, 75F, Path.Direction.CCW)
        canvas?.drawPath(backgroundPath, backgroundPaint)

        //sandaran tangan
        val armrestPath = Path()
        armrestPath.addRect(0F, 0F, 50F, 200F, Path.Direction.CCW)
        canvas?.drawPath(armrestPath, armrestPain)
        canvas?.translate(150F, 0F)
        armrestPath.addRect(0F, 0F, 50F, 200F, Path.Direction.CCW)
        canvas?.drawPath(armrestPath, armrestPain)

        //baigan nama kursi
        canvas?.translate(0F, -175F)
        numberSeatPaint.apply {
            textSize = 50F
            numberSeatPaint.getTextBounds(seat.name, 0 , seat.name.length, mBounds)
        }
        canvas?.drawText(seat.name, mBounds.centerX().toFloat() - 110F, 290F, numberSeatPaint)

        //mengembalikan pengaturan sebelumnya
        canvas?.restore()
    }
}