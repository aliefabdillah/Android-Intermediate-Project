package com.dicoding.storyapp.data

import com.dicoding.storyapp.utils.EventHandlerToast

sealed class Result<out R> private constructor() {
    data class Success<out T>(val data: T): Result<T>()
    data class Error(val error: EventHandlerToast<String>): Result<Nothing>()
    object Loading: Result<Nothing>()
}
