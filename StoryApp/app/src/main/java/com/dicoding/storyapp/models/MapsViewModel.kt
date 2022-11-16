package com.dicoding.storyapp.models

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository

class MapsViewModel(private val storyRepo: StoryRepository): ViewModel() {

    fun getListStoriesWithLocation(token: String, location: Int) = storyRepo.getListStoriesLocation(token, location)

}