package com.dicoding.storyapp.data

import androidx.lifecycle.LiveData
import com.dicoding.storyapp.data.api.ApiService
import com.dicoding.storyapp.data.local.StoriesDao
import com.dicoding.storyapp.data.local.StoryEntity
import com.dicoding.storyapp.data.local.UserPreference

class DataRepository private constructor(
    private val storiesDao: StoriesDao,
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

        @Volatile
        private var instance: DataRepository? = null
        fun getInstance(
            storiesDao: StoriesDao,
        ): DataRepository =
            instance ?: synchronized(this){
                instance ?: DataRepository(storiesDao)
            }.also { instance = it }
    }
}