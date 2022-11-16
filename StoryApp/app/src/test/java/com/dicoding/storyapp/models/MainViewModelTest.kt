package com.dicoding.storyapp.models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.storyapp.adapter.PagingStoryAdapter
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.repository.UserRepository
import com.dicoding.storyapp.data.api.ListStoryItem
import com.dicoding.storyapp.data.local.UserModel
import com.dicoding.storyapp.utils.DataDummy
import com.dicoding.storyapp.utils.MainDispatcherRule
import com.dicoding.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
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
    fun `when get paging list story should not null and Return Success`() = runTest{
        val dummyListStory = DataDummy.generateDummyListStory()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyListStory.listStory)
        val expectedList =  MutableLiveData<PagingData<ListStoryItem>>()
        expectedList.value = data

        `when`(storyRepository.getStoryPaging("token")).thenReturn(expectedList)
        val actualListStory: PagingData<ListStoryItem> = mainViewModel.getStoryPaging("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = PagingStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualListStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyListStory.listStory, differ.snapshot())
        Assert.assertEquals(dummyListStory.listStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyListStory.listStory[0].id, differ.snapshot()[0]?.id)
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

    class StoryPagingSource: PagingSource<Int, LiveData<List<ListStoryItem>>>(){
        companion object{
            fun snapshot(item: List<ListStoryItem>): PagingData<ListStoryItem>{
                return PagingData.from(item)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

}