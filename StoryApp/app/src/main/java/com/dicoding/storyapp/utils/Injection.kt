package com.dicoding.storyapp.utils

import android.content.Context
import com.dicoding.storyapp.data.DataRepository
import com.dicoding.storyapp.data.local.StoryDatabase

object Injection {
    fun provideRepository(context: Context): DataRepository{
        val database = StoryDatabase.getInstance(context)
        val dao = database.storyDao()
        return DataRepository.getInstance(dao)
    }
}