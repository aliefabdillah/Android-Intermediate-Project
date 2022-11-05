package com.dicoding.storyapp.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.api.ApiConfig
import com.dicoding.storyapp.data.api.DetailStoriesResponse
import com.dicoding.storyapp.data.api.ListStoryItem
import com.dicoding.storyapp.data.api.StoriesResponse
import com.dicoding.storyapp.data.local.UserModel
import com.dicoding.storyapp.data.local.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class MainViewModel(private val pref: UserPreference): ViewModel() {

    private val context = Application()

    private val _storiesData = MutableLiveData<List<ListStoryItem>>()
    val storiesData: LiveData<List<ListStoryItem>> = _storiesData

    private val _detailStory = MutableLiveData<ListStoryItem>()
    val detailStory: LiveData<ListStoryItem> = _detailStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<EventHandlerToast<String>>()
    val toastText: LiveData<EventHandlerToast<String>> = _toastText

    fun getListStories(token: String){
        _isLoading.value = true
        val client  =  ApiConfig.getApiService().getStories("Bearer $token")

        client.enqueue(object: retrofit2.Callback<StoriesResponse>{
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _storiesData.value = response.body()?.listStory
                    if (storiesData.value!!.isEmpty()){
                        _toastText.value = EventHandlerToast(context.applicationContext.getString(R.string.list_story_is_empty))
                    }
                }else{
                    _toastText.value = EventHandlerToast(response.message())
                    Log.e(TAG, "Unsuccessfully Response Method: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _toastText.value = EventHandlerToast(t.message.toString())
                Log.e(TAG, "OnFailure in Response Method: ${t.message}")
            }

        })
    }

    fun getDetailStory(token: String, id: String){
        _isLoading.value = true
        val client  =  ApiConfig.getApiService().getDetailsStory("Bearer $token",  id)

        client.enqueue(object: retrofit2.Callback<DetailStoriesResponse>{
            override fun onResponse(
                call: Call<DetailStoriesResponse>,
                response: Response<DetailStoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _detailStory.value = response.body()?.story
                }else{
                    _toastText.value = EventHandlerToast(response.message())
                    Log.e(TAG, "Unsuccessfully Response Method: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailStoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _toastText.value = EventHandlerToast(t.message.toString())
                Log.e(TAG, "OnFailure in Response Method: ${t.message}")
            }

        })
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}