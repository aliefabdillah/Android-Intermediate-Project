package com.dicoding.storyapp.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.DataRepository
import com.dicoding.storyapp.data.api.ListStoryItem
import com.dicoding.storyapp.data.local.StoryEntity
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