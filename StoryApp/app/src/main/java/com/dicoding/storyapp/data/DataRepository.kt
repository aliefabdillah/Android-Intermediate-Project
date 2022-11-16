package com.dicoding.storyapp.data

import androidx.lifecycle.LiveData
import com.dicoding.storyapp.data.api.ListStoryItem
import com.dicoding.storyapp.data.local.StoriesDao
import com.dicoding.storyapp.data.local.StoryEntity

class DataRepository private constructor(
    private val storiesDao: StoriesDao,
){
    fun getStories(): LiveData<List<StoryEntity>> {
        return storiesDao.getStories()
    }

    suspend fun insertStory(listStoryItem: ListStoryItem) {

        val listStory = StoryEntity(
            listStoryItem.id,
            listStoryItem.photoUrl,
            listStoryItem.name,
            listStoryItem.description,
            listStoryItem.lon,
            listStoryItem.lat
        )

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