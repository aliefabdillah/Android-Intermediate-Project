package com.dicoding.mywiddgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class RandomNumberWidget : AppWidgetProvider() {

    /*
    * metode ini dipanggil ketika widget pertama dibuat method ini juga akan
    * update periodMillis pada file xml.
    *
    * Pada metode onUpdate() terdapat perulangan dengan menggunakan array appWidgetIds.
    * Perulangan di sini dimaksudkan untuk menentukan widget mana yang akan di-update karena
    * jumlah widget dalam sebuah aplikasi bisa lebih dari 1. Jadi, kita perlu mendefinisikan
    * widget mana yang perlu diperbarui oleh sistem.*/
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        //cek widget mana yang diklik
        if(WIDGET_CLICK == intent.action){
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val views = RemoteViews(context.packageName, R.layout.random_number_widget)
            val lastUpdate = context.getString(R.string.random_text, NumberGenerator.generate(100))
            val appWidgetId = intent.getIntExtra(WIDGET_ID_EXTRA, 0)   //mengambil data id
            views.setTextViewText(R.id.appwidget_text, lastUpdate)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun getPendingSelfIntent(context: Context, appWidgetId: Int, action: String): PendingIntent{
        val intent = Intent(context, RandomNumberWidget::class.java)
        intent.action = action
        //WIDGET_ID_EXTRA digunakan untuk mengetahui widget id mana yang ditekan sebagai identifier
        intent.putExtra(WIDGET_ID_EXTRA, appWidgetId)
        return PendingIntent.getBroadcast(context, appWidgetId, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                0
            }
        )
    }

    /*
* method yang di panggil setiap perulangan*/
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val lastUpdate = context.getString(R.string.random_text, NumberGenerator.generate(100))
        // Construct the RemoteViews object
        /*Remote view digunakan untuk mengambil data layout dari widget*/
        val views = RemoteViews(context.packageName, R.layout.random_number_widget)
        //mengubah text dari widget
        views.setTextViewText(R.id.appwidget_text, lastUpdate)

        //kode listener inten ketika tombol ditekan
        /* Paremeter kedua merupakan pending intetn yang didapat dari method getPendingSelfIntentm
        * jadi ketika views dengan id btnClick diklik makan akan menjalankan pending intent*/
        views.setOnClickPendingIntent(R.id.btnClick, getPendingSelfIntent(context, appWidgetId,
            WIDGET_CLICK
        ))

        // Instruct the widget manager to update the widget
        /*
        * parameter pertama adalah widget yang ingin di update sedangkang yang ke 2 adalah remoteViews
        * yang berisikan view yang telah dimodifikasi*/
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        private const val WIDGET_CLICK = "android.appwidget.action.APPWIDGET_UPDATE"
        private const val WIDGET_ID_EXTRA = "widget_id_extra"
    }
}

