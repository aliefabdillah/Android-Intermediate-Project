package com.dicoding.storyapp.models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.data.DataRepository
import com.dicoding.storyapp.data.local.StoryEntity
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
class DbViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock private lateinit var dataRepository: DataRepository
    private lateinit var dbViewModel: DbViewModel

    @Before
    fun setUp() {
        dbViewModel = DbViewModel(dataRepository)
    }

    @Test
    fun `when Get Story from database Should Not Null and Return Success`(){
        val dummyListStory = DataDummy.generateDummyListStoryEntity()
        val expectedStory = MutableLiveData<List<StoryEntity>>()
        expectedStory.value = dummyListStory

        `when`(dataRepository.getStories()).thenReturn(expectedStory)

        val actualStory = dbViewModel.getStories().getOrAwaitValue()
        Mockito.verify(dataRepository).getStories()
        Assert.assertNotNull(actualStory)
        Assert.assertEquals(dummyListStory.size, actualStory.size)
    }

    @Test
    fun `when save story to database ensure insertStory method called`() = runTest{
        val dummyResponse = DataDummy.generateDummyDetailStory()
        dbViewModel.saveStoryToDb(dummyResponse)
        Mockito.verify(dataRepository).insertStory(dummyResponse)
    }

    @Test
    fun `when remove all story from database ensure deleteStory method called`() = runTest {
        dbViewModel.deleteAllData()
        Mockito.verify(dataRepository).deleteStory()
    }
}