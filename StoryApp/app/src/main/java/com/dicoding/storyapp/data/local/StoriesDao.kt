package com.dicoding.storyapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoriesDao {

    @Query("SELECT * FROM usersStory")
    fun getStories(): LiveData<List<StoryEntity>>

    //query dengan coroutine
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStories(listStory: StoryEntity)

    @Query("DELETE FROM usersStory")
    suspend fun deleteAllStory()
}