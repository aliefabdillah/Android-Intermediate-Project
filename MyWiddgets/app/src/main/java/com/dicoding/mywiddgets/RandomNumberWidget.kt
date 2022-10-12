package com.dicoding.mywiddgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
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
}

/*
* method yang di panggil setiap perulangan*/
internal fun updateAppWidget(
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

    // Instruct the widget manager to update the widget
    /*
    * parameter pertama adalah widget yang ingin di update sedangkang yang ke 2 adalah remoteViews
    * yang berisikan view yang telah dimodifikasi*/
    appWidgetManager.updateAppWidget(appWidgetId, views)
}