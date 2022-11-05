package com.dicoding.storyapp.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.DataRepository
import com.dicoding.storyapp.data.api.ListStoryItem
import com.dicoding.storyapp.data.local.StoryEntity
import kotlinx.coroutines.launch

class DbViewModel( private val storyRepository: DataRepository): ViewModel() {
    fun getStories() = storyRepository.getUser()

    fun saveStoryToDb(listStoryItem: ListStoryItem){
        val listStory = StoryEntity(
            listStoryItem.id,
            listStoryItem.photoUrl,
            listStoryItem.name,
            listStoryItem.description
        )

        viewModelScope.launch {
            storyRepository.insertStory(listStory)
        }
    }

    fun deleteAllData(){
        viewModelScope.launch {
            storyRepository.deleteStory()
        }
    }
}