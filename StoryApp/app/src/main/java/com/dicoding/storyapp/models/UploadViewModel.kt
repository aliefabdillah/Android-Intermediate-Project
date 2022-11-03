package com.dicoding.storyapp.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapp.data.api.ApiConfig
import com.dicoding.storyapp.data.api.CallbackResponse
import com.dicoding.storyapp.data.local.UserModel
import com.dicoding.storyapp.data.local.UserPreference
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class UploadViewModel(private val pref: UserPreference): ViewModel() {

    private val _error = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<EventHandlerToast<String>>()
    val toastText: LiveData<EventHandlerToast<String>> = _toastText

    fun uploadStory(token: String, imageMultipart: MultipartBody.Part, desc: RequestBody){
        _isLoading.value = true
        val client = ApiConfig.getApiService().uploadStory("Bearer $token", imageMultipart, desc)
        client.enqueue(object : retrofit2.Callback<CallbackResponse>{
            override fun onResponse(
                call: Call<CallbackResponse>,
                response: Response<CallbackResponse>
            ) {
                if (response.isSuccessful){
                    _error.value = response.body()?.error
                    _toastText.value = EventHandlerToast(response.body()?.message.toString())
                }else{
                    val jsonObject = JSONObject(response.errorBody()!!.string())
                    _error.value = jsonObject.getBoolean("error")
                    _toastText.value = EventHandlerToast(jsonObject.getString("message"))
                    Log.e(TAG, "OnFailure in Response Method: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CallbackResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "OnFailure in Failure Method: ${t.message}")
            }
        })
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    companion object{
        private const val TAG = "UploadViewModel"
    }
}