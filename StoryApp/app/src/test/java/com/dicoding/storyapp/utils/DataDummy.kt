package com.dicoding.storyapp.utils

import com.dicoding.storyapp.data.api.*
import com.dicoding.storyapp.data.local.StoryEntity
import com.dicoding.storyapp.data.local.UserModel

object DataDummy {

    fun generateDummyListStory(): StoriesResponse {
        val listStory = ArrayList<ListStoryItem>()
        for (i in 0..10){
            val story = ListStoryItem(
                "ID $i",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-11-16T03:00:59.198Z",
                "Dico",
                "Tes123",
                null,
                null,
            )
            listStory.add(story)
        }

        return StoriesResponse(
            listStory,
            false,
            "Story Fetched Successfully"
        )
    }

    fun generateDummyListStoryWithLocation(): StoriesResponse {
        val listStory = ArrayList<ListStoryItem>()
        for (i in 0..30){
            val story = ListStoryItem(
                "ID $i",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-11-16T03:00:59.198Z",
                "Dico",
                "Tes123",
                -120.7286,
                36.8541
            )
            listStory.add(story)
        }

        return StoriesResponse(
            listStory,
            false,
            "Story Fetched Successfully"
        )
    }

    fun generateDummyListStoryEntity(): List<StoryEntity>{
        val  listStory = ArrayList<StoryEntity>()
        for (i in 0..30){
            val story = StoryEntity(
                "ID $i",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "Dico",
                "tes123",
                -120.7286,
                36.8541
            )
            listStory.add(story)
        }
        return listStory
    }

    fun generateUserLoginResponse(): LoginResult{
        return LoginResult(
            "Dico",
            "ID",
            "TOKEN"
        )
    }

    fun generateDummyDetailStory(): ListStoryItem {

        return ListStoryItem(
            "ID",
            "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
            "2022-11-16T03:00:59.198Z",
            "Dico",
            "Tes123",
            null,
            null,
        )
    }

    fun generateDummyUserDataLogin(): UserModel{
        return UserModel(
            "Dico",
            "token",
            true,
        )
    }

    fun generateDummySuccessResponse(): CallbackResponse{
        return CallbackResponse(
            false,
            "success"
        )
    }

    fun generateDummyFailedResponse(): CallbackResponse{
        return CallbackResponse(
            true,
            "failed"
        )
    }
}