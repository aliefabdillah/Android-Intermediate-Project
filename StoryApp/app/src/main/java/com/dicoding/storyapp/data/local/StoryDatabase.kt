package com.dicoding.storyapp.data.local

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [StoryEntity::class],
    version = 3,
    autoMigrations = [AutoMigration(from = 2, to = 3, spec = StoryDatabase.MyAutoMigration::class)],
    exportSchema = true
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoriesDao

    class MyAutoMigration : AutoMigrationSpec

    companion object {
        @Volatile
        private var instance: StoryDatabase? = null
        fun getInstance(context: Context): StoryDatabase =
            instance ?: synchronized(this){
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java, "DataStory.db"
                ).fallbackToDestructiveMigration().build()
            }
    }
}