package com.dicoding.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.dicoding.storyapp.data.local.StoriesDao
import com.dicoding.storyapp.data.local.StoryEntity
import com.dicoding.storyapp.data.local.UserModel
import com.dicoding.storyapp.data.local.UserPreference

class DataRepository private constructor(
    private val storiesDao: StoriesDao
){
    fun getUser(): LiveData<List<StoryEntity>> {
        return storiesDao.getUser()
    }

    suspend fun insertStory(listStory: StoryEntity) {
        storiesDao.insertStories(listStory)
    }

    suspend fun deleteStory(){
        storiesDao.deleteAllStory()
    }


    companion object {
        private const val TAG = "MainViewModel"

        @Volatile
        private var instance: DataRepository? = null
        fun getInstance(
            storiesDao: StoriesDao
        ): DataRepository =
            instance ?: synchronized(this){
                instance ?: DataRepository(storiesDao)
            }.also { instance = it }
    }
}