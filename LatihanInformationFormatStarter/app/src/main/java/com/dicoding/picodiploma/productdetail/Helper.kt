package com.dicoding.picodiploma.productdetail

import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

//method format number
fun String.withNumberingFormat(): String {
    return NumberFormat.getNumberInstance().format(this.toDouble())
}

//method format date
fun String.withDateFormat(): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    val date = format.parse(this) as Date
    return DateFormat.getDateInstance(DateFormat.FULL).format(date)
}


//method format currency
fun String.withCurrencyFormat(): String {
    val rupiahExchange = 12495.95
    val euroExchange = 0.88

    var priceOnDollar = this.toDouble() / rupiahExchange

    var mCurrencyFormat = NumberFormat.getCurrencyInstance()
    val deviceLocale = Locale.getDefault().country
    when {
        deviceLocale.equals("ES") -> {
            priceOnDollar *= euroExchange
        }
        deviceLocale.equals("ID") -> {
            priceOnDollar *= rupiahExchange
        }
        else -> {
            mCurrencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
        }
    }

    return mCurrencyFormat.format(priceOnDollar)
}