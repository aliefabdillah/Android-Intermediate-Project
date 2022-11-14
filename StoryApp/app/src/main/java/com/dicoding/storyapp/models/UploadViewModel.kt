package com.dicoding.storyapp.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.api.CallbackResponse
import com.dicoding.storyapp.data.local.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadViewModel(private val storyRepo: StoryRepository, private val userRepository: UserRepository): ViewModel() {

    fun uploadStory(
        token: String, imageMultipart: MultipartBody.Part, desc: RequestBody, lat: Double?, lon: Double?
    ): LiveData<Result<CallbackResponse>> = storyRepo.uploadStory(token, imageMultipart, desc, lat, lon)

    fun getUser(): LiveData<UserModel> {
        return userRepository.getUser().asLiveData()
    }

    companion object{
        private const val TAG = "UploadViewModel"
    }
}