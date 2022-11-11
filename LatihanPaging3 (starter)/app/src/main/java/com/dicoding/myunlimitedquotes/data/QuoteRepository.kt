package com.dicoding.myunlimitedquotes.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.dicoding.myunlimitedquotes.database.QuoteDatabase
import com.dicoding.myunlimitedquotes.network.ApiService
import com.dicoding.myunlimitedquotes.network.QuoteResponseItem

class QuoteRepository(private val quoteDatabase: QuoteDatabase, private val apiService: ApiService) {
//    suspend fun getQuote(): List<QuoteResponseItem> {
//        return apiService.getQuote(1, 5)
//    }

    //paging langsung dari network
//    fun getQuote(): LiveData<PagingData<QuoteResponseItem>>{
//        //Pager berfungsi untuk mengolah data dari datasource menjadi pagig data
//        return Pager(
//            config = PagingConfig(pageSize = 5),                    //page size untuk mengatur data perhalaman
//            pagingSourceFactory = { QuotePagingSource(apiService) }
//        ).liveData
//    }

    //paging menggunakan remoteMediator
    fun getQuote(): LiveData<PagingData<QuoteResponseItem>>{
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),                                //jumlah data perhalaman
            //remote mediator dan paging sourfactory untuk menentukan sumber data yang diakses
            remoteMediator = QuoteRemoteMediator(quoteDatabase, apiService),
            pagingSourceFactory = { quoteDatabase.quoteDao().getAllQuote() }
        ).liveData
    }
}