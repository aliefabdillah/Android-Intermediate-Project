package com.dicoding.myunlimitedquotes.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dicoding.myunlimitedquotes.database.QuoteDatabase
import com.dicoding.myunlimitedquotes.database.RemoteKeys
import com.dicoding.myunlimitedquotes.network.ApiService
import com.dicoding.myunlimitedquotes.network.QuoteResponseItem

@OptIn(ExperimentalPagingApi::class)
class QuoteRemoteMediator(
    private val database: QuoteDatabase,
    private val apiService: ApiService
) : RemoteMediator<Int, QuoteResponseItem>(){

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, QuoteResponseItem>
    ): MediatorResult {
        //kode untuk menentukan halaman sesuai load tipe yang terjadi
        val page = when (loadType) {
            LoadType.REFRESH ->{
                /*
                Apabila aplikasi pertama kali dijalankan dengan inisialisasi LAUNCH_INITIAL_REFRESH,
                getRemoteKeyClosestToCurrentPosition akan bernilai null, sehingga page berisi
                INITIAL_PAGE_INDEX bernilai 1.
                * */
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            //condition ini terpanggil ketika scroll ke batas atas dan akan mengambil nilai remote key paling awal
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                //jika nilai remote key sudah bernilai null maka tidak akan melakukan request lagi
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            //kondisi ketika scroll ke batas bawah
            LoadType.APPEND -> {
                //akan mengambil nilai remote key paling bawah di database
                val remoteKeys = getRemoteKeyForLastItem(state)
                //jika sudah mentok maka tidak akan merequest lagi
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            //mengambil response data dari API
            val responseData = apiService.getQuote(page, state.config.pageSize)

            //inisialiasi akhir pagination
            val endOfPaginationReached = responseData.isEmpty()

            database.withTransaction {
                //jika state load adalah refresh
                if (loadType == LoadType.REFRESH) {
                    //menghapus nilai remote key
                    database.remoteKeysDao().deleteRemoteKeys()
                    //menghapus semua data
                    database.quoteDao().deleteAll()
                }

                //jika nilai halaman adalah halaman pertama maka prevKey adalah null jika tidak maka bisa scroll kehalaman sebelumnya
                val prevKey = if (page == 1) null else page - 1
                //jika sudah mencapai akhrir data maka next key bernilai null atau tidak bisa load lagi
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.map {
                    //input nilai prev dan next
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                //input ke db nilai key dan data quote
                database.remoteKeysDao().insertAll(keys)
                database.quoteDao().insertQuote(responseData)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, QuoteResponseItem>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, QuoteResponseItem>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, QuoteResponseItem>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    //method inisialiasi untuk menentukan apakah suatu data yang sudah disimpan kadaluarsa atau tidak
    override suspend fun initialize(): InitializeAction {
        //kode agar data selalu terefresh
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private companion object{
        const val INITIAL_PAGE_INDEX = 1
    }

}