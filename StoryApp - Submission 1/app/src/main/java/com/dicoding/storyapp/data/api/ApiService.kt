package com.dicoding.storyapp.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    //register
    @FormUrlEncoded
    @POST("register")
    fun uploadRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<CallbackResponse>

    //login
    @FormUrlEncoded
    @POST("login")
    fun uploadLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    //getUsers
    @GET("stories?size=20")
    fun getStories(
         @Header("Authorization") token: String
    ): Call<StoriesResponse>

    //getDetails
    @GET("stories/{id}")
    fun getDetailsStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<DetailStoriesResponse>

    //uploadStory
    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<CallbackResponse>

}