package com.dicoding.storyapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usersStory")
class StoryEntity(
    @field:ColumnInfo(name = "storyId")
    @field:PrimaryKey
    val id: String,

    @field:ColumnInfo(name = "photoUrl")
    val photoUrl: String,

    @field:ColumnInfo(name = "username")
    val username: String,

    @field:ColumnInfo(name = "description")
    val desc: String
)