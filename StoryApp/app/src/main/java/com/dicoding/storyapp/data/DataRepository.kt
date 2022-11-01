package com.dicoding.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.dicoding.storyapp.data.local.UserModel
import com.dicoding.storyapp.data.local.UserPreference

class DataRepository private constructor(
    private val pref: UserPreference
){
    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    suspend fun saveUser(userModel: UserModel){
        pref.saveUser(userModel)
    }

    suspend fun logout() {
        pref.logout()
    }

    companion object {
        private const val TAG = "MainViewModel"

        @Volatile
        private var instance: DataRepository? = null
        fun getInstance(
            pref: UserPreference
        ): DataRepository =
            instance ?: synchronized(this){
                instance ?: DataRepository(pref)
            }.also { instance = it }
    }
}