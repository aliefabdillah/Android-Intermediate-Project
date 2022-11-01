package com.dicoding.storyapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dicoding.storyapp.data.DataRepository
import com.dicoding.storyapp.data.local.UserPreference

object Injection {
    fun provideRepository(dataStore: DataStore<Preferences>): DataRepository{
        val preferences = UserPreference.getInstance(dataStore)
        return DataRepository.getInstance(preferences)
    }
}