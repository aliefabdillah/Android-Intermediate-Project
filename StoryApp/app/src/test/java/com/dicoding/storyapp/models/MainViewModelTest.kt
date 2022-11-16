package com.dicoding.storyapp.models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.local.UserModel
import com.dicoding.storyapp.utils.DataDummy
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
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock private lateinit var storyRepository: StoryRepository
    @Mock private lateinit var userRepository: UserRepository
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup(){
        mainViewModel = MainViewModel(storyRepository, userRepository)
    }

    @Test
    fun `when get list story should not null and Return Success`(){
        val dummyListStory = DataDummy.generateDummyListStory()

    }

    @Test
    fun `when get account data not null and return success`(){
        val dummyUserData = DataDummy.generateDummyUserDataLogin()
        val expectedUser = MutableLiveData<UserModel>()
        expectedUser.value = dummyUserData

        `when`(userRepository.getUser()).thenReturn(expectedUser.asFlow())

        val loginUser = mainViewModel.getUser().getOrAwaitValue()
        Mockito.verify(userRepository).getUser()
        Assert.assertNotNull(loginUser)
        Assert.assertEquals(dummyUserData, loginUser)
    }

    @Test
    fun `when logout from accout and delete data account ensure logout method called`() = runTest{
        mainViewModel.logout()
        Mockito.verify(userRepository).logout()
    }

}