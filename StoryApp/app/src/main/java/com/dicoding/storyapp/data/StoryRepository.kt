package com.dicoding.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyapp.data.api.*
import com.dicoding.storyapp.utils.EventHandlerToast
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class StoryRepository private constructor(
    private val apiService: ApiService
){
    private val getListResult = MediatorLiveData<Result<List<ListStoryItem>>>()
    private val getDetailResult = MediatorLiveData<Result<ListStoryItem>>()
    private val uploadResult = MediatorLiveData<Result<CallbackResponse>>()

    fun getListStoriesLocation(token: String, location: Int): LiveData<Result<List<ListStoryItem>>>{
        getListResult.value = Result.Loading
        val client = apiService.getStories(PAGE_SIZE, location, "Bearer $token")

        client.enqueue(object: retrofit2.Callback<StoriesResponse>{
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                if (response.isSuccessful){
                    val response = response.body()
                    if (response != null && !response.error){
                        getListResult.value = Result.Success(response.listStory)
                    }else{
                        getListResult.value = Result.Error(EventHandlerToast(response!!.message))
                        Log.e(TAG, "error in response Method: ${response.message}")
                    }
                }else{
                    val jsonObject = JSONObject(response.errorBody()!!.string())
                    val errorMessage = EventHandlerToast(jsonObject.getString("message"))
                    getListResult.value = Result.Error(errorMessage)
                    Log.e(TAG, "Unsuccessfully Response Method: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                getListResult.value = Result.Error(EventHandlerToast(t.message.toString()))
                Log.e(TAG, "OnFailure in Response Method: ${t.message}")
            }

        })

        return getListResult
    }

    fun getStoryPaging(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { StoryPagingSource(apiService, token) }
        ).liveData
    }

    fun getDetailStory(token: String, id: String): LiveData<Result<ListStoryItem>>{
        getDetailResult.value = Result.Loading
        val client  =  apiService.getDetailsStory("Bearer $token",  id)

        client.enqueue(object: retrofit2.Callback<DetailStoriesResponse>{
            override fun onResponse(
                call: Call<DetailStoriesResponse>,
                response: Response<DetailStoriesResponse>
            ) {
                if (response.isSuccessful){
                    val response = response.body()
                    if (response != null && !response.error){
                        getDetailResult.value = Result.Success(response.story)
                    }else{
                        getDetailResult.value = Result.Error(EventHandlerToast(response!!.message))
                        Log.e(TAG, "error in response: ${response.message}")
                    }
                }else{
                    val jsonObject = JSONObject(response.errorBody()!!.string())
                    val errorMessage = EventHandlerToast(jsonObject.getString("message"))
                    getDetailResult.value = Result.Error(errorMessage)
                    Log.e(TAG, "Unsuccessfully Response Method: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailStoriesResponse>, t: Throwable) {
                getDetailResult.value = Result.Error(EventHandlerToast(t.message.toString()))
                Log.e(TAG, "OnFailure in Response Method: ${t.message}")
            }
        })

        return getDetailResult
    }

    fun uploadStory(
        token: String,
        imageMultipart: MultipartBody.Part,
        desc: RequestBody,
        lat: Double?,
        lon: Double?,
    ): LiveData<Result<CallbackResponse>> {
        uploadResult.value = Result.Loading

        val client = apiService.uploadStory("Bearer $token", imageMultipart, desc, lat, lon)
        client.enqueue(object : retrofit2.Callback<CallbackResponse>{
            override fun onResponse(
                call: Call<CallbackResponse>,
                response: Response<CallbackResponse>
            ) {
                if (response.isSuccessful){
                    val response = response.body()
                    if (response != null && !response.error){
                        uploadResult.value = Result.Success(response)
                    }else{
                        uploadResult.value = Result.Error(EventHandlerToast(response!!.message))
                        Log.e(TAG, "error in response: ${response.message}")
                    }
                }else{
                    val jsonObject = JSONObject(response.errorBody()!!.string())
                    val errorMessage = EventHandlerToast(jsonObject.getString("message"))
                    uploadResult.value = Result.Error(errorMessage)
                    Log.e(TAG, "OnFailure in Response Method: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CallbackResponse>, t: Throwable) {
                uploadResult.value = Result.Error(EventHandlerToast(t.message.toString()))
                Log.e(TAG, "OnFailure in Failure Method: ${t.message}")
            }
        })

        return uploadResult
    }

    companion object {
        private const val TAG = "MainViewModel"
        private const val PAGE_SIZE = 30

        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): StoryRepository =
            instance ?: synchronized(this){
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }
}