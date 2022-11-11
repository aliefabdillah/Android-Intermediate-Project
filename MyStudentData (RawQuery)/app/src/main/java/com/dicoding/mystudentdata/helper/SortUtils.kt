package com.dicoding.mystudentdata.helper

import androidx.sqlite.db.SimpleSQLiteQuery

object SortUtils {
    //method membuat query dan mengembalikan nilai beruba SimpleSQLitequery
    fun getSortedQuery(sortType: SortType): SimpleSQLiteQuery{
        val simpleQuery = StringBuilder().append("SELECT * FROM student ")
        when(sortType){
            SortType.ASCENDING -> simpleQuery.append("ORDER BY name ASC")       //jika sort tipe asc
            SortType.DESCENDING -> simpleQuery.append("ORDER BY name DESC")     //jika sort tipe desc
            SortType.RANDOM -> simpleQuery.append("ORDER BY RANDOM()")          //jika sort tipe random()
        }
        return SimpleSQLiteQuery(simpleQuery.toString())
    }
}