package com.dicoding.storyapp.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.paging.LoadState
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyapp.data.api.ApiService
import com.dicoding.storyapp.data.api.ListStoryItem
import com.dicoding.storyapp.data.api.StoriesResponse
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response

class StoryPagingSource(private val apiService: ApiService, private val token: String): PagingSource<Int, ListStoryItem>() {

    private lateinit var listStory : List<ListStoryItem>
    private lateinit var context: Context

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {

            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStoriesPaging(position, params.loadSize, "Bearer $token")
            listStory = responseData.listStory

            LoadResult.Page(
                data = listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (listStory.isNullOrEmpty()) null else position + 1
            )
        }catch (exception: Exception){
            return LoadResult.Error(exception)
        }catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}