package com.dicoding.storyapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dicoding.storyapp.data.DataRepository
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.api.ApiConfig
import com.dicoding.storyapp.data.local.StoryDatabase
import com.dicoding.storyapp.data.local.UserPreference

object Injection {
    fun provideDataRepository(context: Context): DataRepository{
        val database = StoryDatabase.getInstance(context)
        val dao = database.storyDao()
        return DataRepository.getInstance(dao)
    }

    fun provideStoryRepository(): StoryRepository{
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService)
    }

    fun provideUserRepository(dataStore: DataStore<Preferences>): UserRepository{
        val apiService = ApiConfig.getApiService()
        val pref = UserPreference.getInstance(dataStore)
        return UserRepository.getInstance(apiService, pref)
    }
}