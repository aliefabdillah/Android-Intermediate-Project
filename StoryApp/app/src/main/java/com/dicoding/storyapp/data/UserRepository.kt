package com.dicoding.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dicoding.storyapp.data.api.ApiService
import com.dicoding.storyapp.data.api.CallbackResponse
import com.dicoding.storyapp.data.api.LoginResponse
import com.dicoding.storyapp.data.api.LoginResult
import com.dicoding.storyapp.data.local.UserModel
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.utils.EventHandlerToast
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
){
    private val signInResult = MediatorLiveData<Result<LoginResult>>()
    private val signUpResult = MediatorLiveData<Result<CallbackResponse>>()

    fun signInUser(email: String, pass: String): LiveData<Result<LoginResult>>{
        signInResult.value = Result.Loading

        val client = apiService.uploadLogin(email, pass)

        client.enqueue(object : retrofit2.Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    val response = response.body()!!
                    if (!response.error){
                        val loginResult = LoginResult(
                           response.loginResult.name,
                           response.loginResult.userId,
                           response.loginResult.token
                        )
                        signInResult.value = Result.Success(loginResult)
                    }else{
                        signInResult.value = Result.Error(EventHandlerToast(response.message))
                        Log.e(TAG, "error in OnResponse Method: ${response.message}")
                    }
                }else{
                    val jsonObject = JSONObject(response.errorBody()!!.string())
                    val errorMessage = EventHandlerToast(jsonObject.getString("message"))
                    signInResult.value = Result.Error(errorMessage)
                    Log.e(TAG, "response not successful in onResponse Method: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                signInResult.value = Result.Error(EventHandlerToast(t.message.toString()))
                Log.e(TAG, "Failure  in onFailure Method: ${t.message}")
            }

        })

        return signInResult
    }

    fun signUpUser(name: String, email: String, pass: String): LiveData<Result<CallbackResponse>> {
        signUpResult.value = Result.Loading
        val client = apiService.uploadRegister(name, email, pass)

        client.enqueue(object : retrofit2.Callback<CallbackResponse>{
            override fun onResponse(call: Call<CallbackResponse>, response: Response<CallbackResponse>) {
                if (response.isSuccessful){
                    val response = response.body()!!
                    if (!response.error){
                        signUpResult.value = Result.Success(response)
                    }else{
                        signUpResult.value = Result.Error(EventHandlerToast(response.message))
                        Log.e(TAG, "error in onResponse Method: ${response.message}")
                    }
                }else{
                    val jsonObject = JSONObject(response.errorBody()!!.string())
                    val errorMessage = jsonObject.getString("message")
                    signUpResult.value = Result.Error(EventHandlerToast(errorMessage))
                    Log.e(TAG, "unsuccessfull response in onResponse Method: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CallbackResponse>, t: Throwable) {
                signUpResult.value = Result.Error(EventHandlerToast(t.message.toString()))
                Log.e(TAG, "OnFailure in Failure Method: ${t.message}")
            }
        })

        return signUpResult
    }

    suspend fun saveUser(user: UserModel){
        userPreference.saveUser(user)
    }

    fun getUser() = userPreference.getUser()

    suspend fun logout(){
        userPreference.logout()
    }

    companion object {
        private const val TAG = "LoginViewModel"

        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            preference: UserPreference,
        ): UserRepository =
            instance ?: synchronized(this){
                instance ?: UserRepository(apiService, preference)
            }.also { instance = it }
    }
}