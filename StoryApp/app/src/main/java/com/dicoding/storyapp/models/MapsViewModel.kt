package com.dicoding.storyapp.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.local.UserModel

class MapsViewModel(private val storyRepo: StoryRepository, private val userRepository: UserRepository): ViewModel() {

    fun getListStoriesWithLocation(token: String, location: Int) = storyRepo.getListStoriesLocation(token, location)

    fun getUser(): LiveData<UserModel> {
        return userRepository.getUser().asLiveData()
    }
}