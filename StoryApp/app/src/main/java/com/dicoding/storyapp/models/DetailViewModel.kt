package com.dicoding.storyapp.models

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.StoryRepository

class DetailViewModel(private val storyRepo: StoryRepository): ViewModel() {
    fun getDetailStory(token: String, id: String) = storyRepo.getDetailStory(token, id)
}