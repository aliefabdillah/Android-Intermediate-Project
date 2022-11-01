package com.dicoding.storyapp.data.api

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
    @Headers("Authorization: token ")
    @GET("stories")
    fun getStories(
        @Header("Authorization") value: String
    ): Call<StoriesResponse>
}