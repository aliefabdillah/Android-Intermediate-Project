package com.dicoding.myunlimitedquotes.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)

/*
Kelas ini berfungsi untuk menyimpan informasi tentang halaman terbaru yang diminta dari server.
Dengan informasi ini, aplikasi dapat mengidentifikasi dan meminta halaman data yang tepat pada halaman
selanjutnya.
* */