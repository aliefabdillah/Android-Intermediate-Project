package com.dicoding.storyapp.models

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.UserRepository

class SignUpViewModel(private val userRepo: UserRepository) : ViewModel() {
    fun signUpUser(name: String, email: String, pass: String) = userRepo.signUpUser(name, email, pass)
}