package com.dicoding.mywiddgets

import java.util.*

/*
* Class ini bertipe object karena sifatnya yang static jadi tidak
* perlu inisialiasi untuk menggunakannya
*
* Kelas NumberGenerator merupakan kelas helper yang memudahkan kita untuk membuat
* angka secara acak. Inputan berupa bilangan bertipe integer digunakan sebagai nilai maksimum
* yang bisa dicapai oleh fungsi generator kita. Generator angka dimulai dari 0 hingga nilai
* maksimum.  */
internal object NumberGenerator {
    fun generate(max: Int): Int{
        val random = Random()
        return random.nextInt(max)
    }
}