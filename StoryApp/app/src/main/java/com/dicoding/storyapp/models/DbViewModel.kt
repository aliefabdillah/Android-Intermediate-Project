package com.dicoding.storyapp.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.repository.DataRepository
import com.dicoding.storyapp.data.api.ListStoryItem
import kotlinx.coroutines.launch

class DbViewModel( private val dataRepository: DataRepository): ViewModel() {
    fun getStories() = dataRepository.getStories()

    fun saveStoryToDb(listStoryItem: ListStoryItem){

        viewModelScope.launch {
            dataRepository.insertStory(listStoryItem)
        }
    }

    fun deleteAllData(){
        viewModelScope.launch {
            dataRepository.deleteStory()
        }
    }
}