package com.dicoding.storyapp.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.local.UserModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val storyRepo: StoryRepository, private val userRepository: UserRepository): ViewModel() {

    fun getListStories(token: String) = storyRepo.getListStories(token)

    fun getDetailStory(token: String, id: String) = storyRepo.getDetailStory(token, id)

    fun uploadStory(
        token: String, imageMultipart: MultipartBody.Part, desc: RequestBody
    ) = storyRepo.uploadStory(token, imageMultipart, desc)


    fun getUser(): LiveData<UserModel> {
        return userRepository.getUser().asLiveData()
    }

    fun logout(){
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    /*private val context = Application()

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
    }*/

}