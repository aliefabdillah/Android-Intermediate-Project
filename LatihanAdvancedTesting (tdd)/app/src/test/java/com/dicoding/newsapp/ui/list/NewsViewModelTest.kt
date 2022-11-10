package com.dicoding.newsapp.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.newsapp.utils.DataDummy
import com.dicoding.newsapp.data.NewsRepository
import com.dicoding.newsapp.data.Result
import com.dicoding.newsapp.data.local.entity.NewsEntity
import com.dicoding.newsapp.utils.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

//kelas yang digunakan untuk membuat dan memvalidasi proses mock
@RunWith(MockitoJUnitRunner::class)
class NewsViewModelTest{

    //rule agar kelas ini dijalankan secara synchronus
    /*
    dengan menggunakan metode tersebut kode ketika mengambil live data akan dieksekusi terlebih dahulu
    sampai selesai baru lanjut ke prorses berikutnya
     */
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    //membuat object mock atau object tiruan
    @Mock
    private lateinit var newsRepository: NewsRepository
    private lateinit var newsViewModel: NewsViewModel
    private val dummyNews = DataDummy.generateDummyNewsEntity()

    @Before
    fun setup(){
        newsViewModel = NewsViewModel(newsRepository)
    }

    //testing return dari method pada viewmodel
    @Test
    fun `when Get HeadlineNews Should Not Null and Return Success`() {
        val expectedNews = MutableLiveData<Result<List<NewsEntity>>>()
        expectedNews.value = Result.Success(dummyNews)

        //membuat sebuah stub
        //“Ketika newsRepository.getHeadlineNews dipanggil, maka akan mengembalikan nilai dari expectedNews.”
        //when disini untuk membedakan dengan when (conditioN)
        `when`(newsRepository.getHeadlineNews()).thenReturn(expectedNews)

        //mengambil data dengan menerapkan fungsi di livedataUtil yang memiliki method Observe
        val actualNews = newsViewModel.getHeadlineNews().getOrAwaitValue()

        Mockito.verify(newsRepository).getHeadlineNews()
        Assert.assertNotNull(actualNews)
        Assert.assertTrue(actualNews is Result.Success)
        Assert.assertEquals(dummyNews.size, (actualNews as Result.Success).data.size)
    }

    //testing ketika network error
    @Test
    fun `when Network Error Should Return Error`() {
        val headlineNews = MutableLiveData<Result<List<NewsEntity>>>()
        headlineNews.value = Result.Error("Error")

        `when`(newsRepository.getHeadlineNews()).thenReturn(headlineNews)

        val actualNews = newsViewModel.getHeadlineNews().getOrAwaitValue()
        Mockito.verify(newsRepository).getHeadlineNews()
        Assert.assertNotNull(actualNews)
        Assert.assertTrue(actualNews is Result.Error)
    }
}