package com.dicoding.storyapp.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.local.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val storyRepo: StoryRepository, private val userRepository: UserRepository): ViewModel() {

    fun getListStories(token: String) = storyRepo.getListStories(token)

    fun getUser(): LiveData<UserModel> {
        return userRepository.getUser().asLiveData()
    }

    fun logout(){
        viewModelScope.launch {
            userRepository.logout()
        }
    }

}