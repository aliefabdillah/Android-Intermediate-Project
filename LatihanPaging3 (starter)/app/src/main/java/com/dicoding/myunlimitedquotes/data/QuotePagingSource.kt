package com.dicoding.myunlimitedquotes.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.myunlimitedquotes.network.ApiService
import com.dicoding.myunlimitedquotes.network.QuoteResponseItem

class QuotePagingSource(private val apiService: ApiService): PagingSource<Int, QuoteResponseItem>() {
    override fun getRefreshKey(state: PagingState<Int, QuoteResponseItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
        /*
        Fungsi getRefreshKey berguna untuk mengetahui kapan mengambil data terbaru dan menggantikan
        data yang sudah tampil. Biasanya, proses refresh ini dilakukan di sekitar PagingState.anchorPosition,
        yakni indeks yang paling baru diakses. Apabila prevKey bernilai null, anchorPage-nya adalah
        halaman pertama. Lalu, apabila nextKey bernilai null, anchorPage-nya adalah halaman terakhir,
        sedangkan jika kedua nilai prevKey dan nextKey bernilai null, anchorPage-nya adalah initial page.
        * */
    }

    //method meload data selanjutnya
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuoteResponseItem> {
        return try{
            //ketika pertama kali app dijalankan page akan bernilai null dan mengambil nilai dari INITIAL PAGE yaitu 1
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getQuote(position, params.loadSize)

            LoadResult.Page(
                data = responseData,
                //nilai prevKey adalah kode halaman sebelumnya
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                //nilai next key adalah kode halaman setelahnya dan akan terus bertambah sampai tidak ada halaman yang bisa diload
                nextKey = if (responseData.isNullOrEmpty()) null else position + 1
            )
        }catch (e: Exception){
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}