package com.dicoding.storyapp.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.data.local.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepo: UserRepository): ViewModel() {
    //method save data user ke prefrences
    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            userRepo.saveUser(user)
        }
    }

    fun signInUser(email: String, pass: String) = userRepo.signInUser(email, pass)
}