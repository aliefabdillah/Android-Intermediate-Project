package com.dicoding.storyapp.models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.api.LoginResponse
import com.dicoding.storyapp.data.api.LoginResult
import com.dicoding.storyapp.data.local.UserModel
import com.dicoding.storyapp.utils.DataDummy
import com.dicoding.storyapp.utils.EventHandlerToast
import com.dicoding.storyapp.utils.MainDispatcherRule
import com.dicoding.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setup(){
        loginViewModel = LoginViewModel(userRepository)
    }

    @Test
    fun `when user login should not null and Return Success`(){
        val dummyResponse = DataDummy.generateUserLoginResponse()
        val expectedResponse = MutableLiveData<Result<LoginResult>>()
        expectedResponse.value = Result.Success(dummyResponse)

        `when`(userRepository.signInUser(EMAIL, PASS)).thenReturn(expectedResponse)

        val actualData = loginViewModel.signInUser(EMAIL, PASS).getOrAwaitValue()
        Mockito.verify(userRepository).signInUser(EMAIL, PASS)
        Assert.assertNotNull(actualData)
        Assert.assertTrue(actualData is Result.Success)
    }

    @Test
    fun `when user login error should not null and Return Error`(){
        val expectedResponse = MutableLiveData<Result<LoginResult>>()
        expectedResponse.value = Result.Error(EventHandlerToast("error"))

        `when`(userRepository.signInUser(EMAIL, PASS)).thenReturn(expectedResponse)

        val actualData = loginViewModel.signInUser(EMAIL, PASS).getOrAwaitValue()
        Mockito.verify(userRepository).signInUser(EMAIL, PASS)
        Assert.assertNotNull(actualData)
        Assert.assertTrue(actualData is Result.Error)
    }

    @Test
    fun `when user login success should save user method called`() = runTest{
        val dummyData = DataDummy.generateDummyUserDataLogin()
        loginViewModel.saveUser(dummyData)
        Mockito.verify(userRepository).saveUser(dummyData)
    }

    companion object{
        const val EMAIL = "email"
        const val PASS = "pass"
    }
}