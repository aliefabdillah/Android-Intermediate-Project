package com.dicoding.storyapp.models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.utils.MainDispatcherRule
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.api.ListStoryItem
import com.dicoding.storyapp.utils.DataDummy
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
class DetailViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var detailViewModel: DetailViewModel

    @Before
    fun setup(){
        detailViewModel = DetailViewModel(storyRepository)
    }

    @Test
    fun `when Get Detail Story should not null and Return Success`() {
        val dummyData = DataDummy.generateDummyDetailStory()
        val expectedDetailStory = MutableLiveData<Result<ListStoryItem>>()
        expectedDetailStory.value = Result.Success(dummyData)

        `when`(storyRepository.getDetailStory(TOKEN, ID)).thenReturn(expectedDetailStory)

        val actualDetailStory = detailViewModel.getDetailStory(TOKEN, ID).getOrAwaitValue()

        Mockito.verify(storyRepository).getDetailStory(TOKEN, ID)
        Assert.assertNotNull(actualDetailStory)
        Assert.assertTrue(actualDetailStory is Result.Success)
        Assert.assertEquals(dummyData, (actualDetailStory as Result.Success).data)
    }

    @Test
    fun `when Get Detail Story failed should not null and Return Error`(){
        val expectedDetailStory = MutableLiveData<Result<ListStoryItem>>()
        expectedDetailStory.value = Result.Error(EventHandlerToast("Error"))

        `when`(storyRepository.getDetailStory(TOKEN, ID)).thenReturn(expectedDetailStory)

        val actualStory = detailViewModel.getDetailStory(TOKEN, ID).getOrAwaitValue()
        Mockito.verify(storyRepository).getDetailStory(TOKEN, ID)
        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is Result.Error)
    }

    companion object{
        const val TOKEN = "TOKEN"
        const val ID = "ID"
    }


}