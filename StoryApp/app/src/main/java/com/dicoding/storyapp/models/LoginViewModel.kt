package com.dicoding.storyapp.models

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.storyapp.data.api.ApiConfig
import com.dicoding.storyapp.data.api.LoginResponse
import com.dicoding.storyapp.data.api.LoginResult
import com.dicoding.storyapp.data.local.UserModel
import com.dicoding.storyapp.data.local.UserPreference
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference): ViewModel() {
    private val _error = MutableLiveData<Boolean>()
    var error: LiveData<Boolean> = _error

    private val _userData = MutableLiveData<LoginResult>()
    var userData: LiveData<LoginResult> = _userData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<EventHandlerToast<String>>()
    val toastText: LiveData<EventHandlerToast<String>> = _toastText

    //method save data user ke prefrences
    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }


    fun signInUser(email: String, pass: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().uploadLogin(email, pass)

        client.enqueue(object : retrofit2.Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _error.value = response.body()?.error
                    _toastText.value = EventHandlerToast(response.body()?.message.toString())
                    _userData.value = response.body()?.loginResult
                }else{
                    val jsonObject = JSONObject(response.errorBody()!!.string())
                    _error.value = jsonObject.getBoolean("error")
                    _toastText.value = EventHandlerToast(jsonObject.getString("message"))
                    Log.e(TAG, "OnFailure in Response Method: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "OnFailure  in Failure Method: ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}