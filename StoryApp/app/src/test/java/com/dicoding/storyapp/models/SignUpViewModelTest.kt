package com.dicoding.storyapp.models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.utils.DataDummy
import com.dicoding.storyapp.utils.MainDispatcherRule
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.api.CallbackResponse
import com.dicoding.storyapp.utils.EventHandlerToast
import com.dicoding.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class SignUpViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock private lateinit var userRepository: UserRepository
    private lateinit var signUpViewModel: SignUpViewModel

    @Before
    fun setUp() {
        signUpViewModel = SignUpViewModel(userRepository)
    }

    @Test
    fun `when user register success should response not null and Return Success`(){
        val dummyResponse = DataDummy.generateDummySuccessResponse()
        val expectedResponse = MutableLiveData<Result<CallbackResponse>>()
        expectedResponse.value = Result.Success(dummyResponse)

        `when`(userRepository.signUpUser(NAME, EMAIL, PASS)).thenReturn(expectedResponse)

        val actualResponse = signUpViewModel.signUpUser(NAME, EMAIL, PASS).getOrAwaitValue()
        Mockito.verify(userRepository).signUpUser(NAME, EMAIL, PASS)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `when user register failed should response not null and Return Error`(){
        val dummyResponse = DataDummy.generateDummyFailedResponse()
        val expectedResponse = MutableLiveData<Result<CallbackResponse>>()
        expectedResponse.value = Result.Error(EventHandlerToast(dummyResponse.message))

        `when`(userRepository.signUpUser(NAME, EMAIL, PASS)).thenReturn(expectedResponse)

        val actualResponse = signUpViewModel.signUpUser(NAME, EMAIL, PASS).getOrAwaitValue()
        Mockito.verify(userRepository).signUpUser(NAME, EMAIL, PASS)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Error)
    }

    companion object{
        const val NAME = "name"
        const val EMAIL = "email"
        const val PASS = "pass"
    }
}