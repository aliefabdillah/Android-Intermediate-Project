package com.dicoding.mystackview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf

internal class StackRemoteViewsFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()

    override fun onCreate() {

    }

    /* proses method ini dapat digunakan untuk memuat semua data yang akan digunakan pada widget
    *  proses load di method ini harus kurang dari 20 detik jika tidak akan terjadi not responding*/
    //Ini berfungsi untuk melakukan refresh saat terjadi perubahan.
    override fun onDataSetChanged() {
        mWidgetItems.add(BitmapFactory.decodeResource(mContext.resources, R.drawable.darth_vader))
        mWidgetItems.add(BitmapFactory.decodeResource(mContext.resources, R.drawable.star_wars_logo))
        mWidgetItems.add(BitmapFactory.decodeResource(mContext.resources, R.drawable.storm_trooper))
        mWidgetItems.add(BitmapFactory.decodeResource(mContext.resources, R.drawable.starwars))
        mWidgetItems.add(BitmapFactory.decodeResource(mContext.resources, R.drawable.falcon))
    }

    /* NOTES!!!
    * Ketika Anda menggunakan Widget untuk menampilkan data dari database, Anda perlu menambahkan
    * kode-kode berikut agar tidak force close dan data akan muncul secara realtime:
    override fun onDataSetChanged() {
        if (cursor != null) {
            cursor.close()
        }

        val identityToken = Binder.clearCallingIdentity()

        // querying ke database
        cursor = context.getContentResolver().query(CONTENT_URI, null, null, null, null)

        Binder.restoreCallingIdentity(identityToken)
    }
    * */

    override fun onDestroy() {
    }

    //mengembalikan nilai jumlah isi data pada widget yang ditampilkan
    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(pos: Int): RemoteViews {

        //memaskan gambar bitmap memanfaatkan RemoteViews
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[pos])

        val extras = bundleOf(ImageBannerWidget.EXTRA_ITEM to pos)

        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    /* pada method ini nilai kembaliannya harus lebih dari 0 karena nilai ini mewakili
    * jumlah layout item yang digunakan pada widget*/
    override fun getViewTypeCount(): Int  = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}