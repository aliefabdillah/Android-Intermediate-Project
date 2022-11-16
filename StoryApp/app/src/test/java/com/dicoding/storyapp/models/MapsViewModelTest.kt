package com.dicoding.storyapp.models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.api.ListStoryItem
import com.dicoding.storyapp.utils.DataDummy
import com.dicoding.storyapp.utils.EventHandlerToast
import com.dicoding.storyapp.utils.MainDispatcherRule
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
class MapsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mapsViewModel: MapsViewModel

    @Before
    fun setUp() {
        mapsViewModel = MapsViewModel(storyRepository)
    }

    @Test
    fun `when Get List Story With Location should not null and Return Success`(){
        val dummyStoryWithLocation = DataDummy.generateDummyListStoryWithLocation()
        val expectedStory = MutableLiveData<Result<List<ListStoryItem>>>()
        expectedStory.value = Result.Success(dummyStoryWithLocation.listStory)

        `when`(storyRepository.getListStoriesLocation(TOKEN, LOCATION)).thenReturn(expectedStory)

        val actualListStory = mapsViewModel.getListStoriesWithLocation(TOKEN, LOCATION).getOrAwaitValue()

        Mockito.verify(storyRepository).getListStoriesLocation(TOKEN, LOCATION)
        Assert.assertNotNull(actualListStory)
        Assert.assertTrue(actualListStory is Result.Success)
        Assert.assertEquals(dummyStoryWithLocation.listStory.size, (actualListStory as Result.Success).data.size)
    }

    @Test
    fun `when Get List Story With Location failed should not null and Return Error`(){
        val expectedStory = MutableLiveData<Result<List<ListStoryItem>>>()
        expectedStory.value = Result.Error(EventHandlerToast("Error"))

        `when`(storyRepository.getListStoriesLocation(TOKEN, LOCATION)).thenReturn(expectedStory)

        val actualListStory = mapsViewModel.getListStoriesWithLocation(TOKEN, LOCATION).getOrAwaitValue()
        Mockito.verify(storyRepository).getListStoriesLocation(TOKEN, LOCATION)
        Assert.assertNotNull(actualListStory)
        Assert.assertTrue(actualListStory is Result.Error)
    }

    companion object{
        const val TOKEN = "TOKEN"
        const val LOCATION = 1
    }
}