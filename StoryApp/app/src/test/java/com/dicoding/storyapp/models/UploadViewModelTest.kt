package com.dicoding.storyapp.models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.data.api.CallbackResponse
import com.dicoding.storyapp.data.local.UserModel
import com.dicoding.storyapp.utils.DataDummy
import com.dicoding.storyapp.utils.EventHandlerToast
import com.dicoding.storyapp.utils.MainDispatcherRule
import com.dicoding.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UploadViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var uploadViewModel: UploadViewModel

    @Before
    fun setUp() {
        uploadViewModel = UploadViewModel(storyRepository, userRepository)
    }

    @Test
    fun `when user upload story should response not null and Return Success`(){
        val dummyResponse = DataDummy.generateDummySuccessResponse()
        val expectedResponse = MutableLiveData<Result<CallbackResponse>>()
        expectedResponse.value = Result.Success(dummyResponse)

        val file = mock(File::class.java)
        val imageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            imageFile
        )
        val desc = "tes123".toRequestBody("text/plain".toMediaTypeOrNull())
        val lat = 36.8541
        val lon = -120.7286

        `when`(storyRepository.uploadStory(TOKEN, imageMultipart, desc, lat, lon)).thenReturn(expectedResponse)

        val actualResponse = uploadViewModel.uploadStory(TOKEN, imageMultipart, desc, lat, lon).getOrAwaitValue()
        Mockito.verify(storyRepository).uploadStory(TOKEN, imageMultipart, desc, lat, lon)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `when user upload story failed should response not null and Return Error`(){
        val dummyResonse = DataDummy.generateDummyFailedResponse()
        val expectedResponse = MutableLiveData<Result<CallbackResponse>>()
        expectedResponse.value = Result.Error(EventHandlerToast(dummyResonse.message))

        val file = mock(File::class.java)
        val imageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            imageFile
        )
        val desc = "tes123".toRequestBody("text/plain".toMediaTypeOrNull())
        val lat = 36.8541
        val lon = -120.7286

        `when`(storyRepository.uploadStory(TOKEN, imageMultipart, desc, lat, lon)).thenReturn(expectedResponse)

        val actualResponse = uploadViewModel.uploadStory(TOKEN, imageMultipart, desc, lat, lon).getOrAwaitValue()
        Mockito.verify(storyRepository).uploadStory(TOKEN, imageMultipart, desc, lat, lon)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Error)
    }

    @Test
    fun `when get account data not null and return success`(){
        val dummyUserData = DataDummy.generateDummyUserDataLogin()
        val expectedUser = MutableLiveData<UserModel>()
        expectedUser.value = dummyUserData

        `when`(userRepository.getUser()).thenReturn(expectedUser.asFlow())

        val loginUser = uploadViewModel.getUser().getOrAwaitValue()
        Mockito.verify(userRepository).getUser()
        Assert.assertNotNull(loginUser)
        Assert.assertEquals(dummyUserData, loginUser)
    }

    companion object {
        const val TOKEN = ""
    }
}