package com.dicoding.storyapp.models

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.repository.DataRepository
import com.dicoding.storyapp.utils.Injection

class DbViewModelFactory(private val storyRepository: DataRepository): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DbViewModel::class.java) -> {
                DbViewModel(storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: DbViewModelFactory? = null
        fun getInstance(context: Context): DbViewModelFactory =
            instance ?: synchronized(this){
                instance ?: DbViewModelFactory(Injection.provideDataRepository(context))
            }.also { instance = it }
    }
}