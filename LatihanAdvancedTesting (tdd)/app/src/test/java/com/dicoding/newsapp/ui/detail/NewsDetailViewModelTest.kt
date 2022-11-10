package com.dicoding.newsapp.ui.detail

/*
Scenario awal:
Pada studi kasus kali ini, Anda tidak melakukan pengecekan hasil data karena tidak ada nilai kembalian
dari fungsi saveNews dan deleteNews. Yang kita uji adalah logika pada fungsi changeBookmark, apakah
ia memanggil fungsi yang tepat sesuai dengan kondisi yang diberikan. Berikut contoh kondisinya.

- Ketika mengubah data bookmark.
    -Apabila bookmarkStatus bernilai false, ia akan memanggil fungsi saveNews.
    -Apabila bookmarkStatus bernilai true, ia akan memanggil fungsi deleteNews.
* */
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.newsapp.data.NewsRepository
import com.dicoding.newsapp.utils.DataDummy
import com.dicoding.newsapp.utils.MainDispatcherRule
import com.dicoding.newsapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
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
class NewsDetailViewModelTest{

    @get:Rule
    val instanceExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var newsRepository: NewsRepository
    private lateinit var newsDetailViewModel: NewsDetailViewModel
    private val dummyDetailNews = DataDummy.generateDummyNewsEntity()[0]

    @Before
    fun  setup(){
        newsDetailViewModel = NewsDetailViewModel(newsRepository)
        newsDetailViewModel.setNewsData(dummyDetailNews)

    }

    //menggunakan MainDispatcherRule
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    //testing menyimpan data bookmark
    @Test
    fun `when bookmark status false should call saveNews`() = runTest {
        val expectedBoolean = MutableLiveData<Boolean>()
        expectedBoolean.value = false

        //stub ketika isBookmark dipanggil
        `when`(newsRepository.isNewsBookmarked(dummyDetailNews.title)).thenReturn(expectedBoolean)
        //observer data bookmarkStatus
        newsDetailViewModel.bookmarkStatus.getOrAwaitValue()
        //memanggil fungsi changeBookmark
        newsDetailViewModel.changeBookmark(dummyDetailNews)
        //verifikasi fungsi sudah terpanggil atau tidak
        Mockito.verify(newsRepository).saveNews(dummyDetailNews)
    }

    //testing menghapus data bookmark
    @Test
    fun `when bookmarkStatus true Should call deleteNews`() = runTest {
        val expectedBoolean = MutableLiveData<Boolean>()
        expectedBoolean.value = true

        //stub ketika isBookmark dipanggil
        `when`(newsRepository.isNewsBookmarked(dummyDetailNews.title)).thenReturn(expectedBoolean)
        newsDetailViewModel.bookmarkStatus.getOrAwaitValue()
        newsDetailViewModel.changeBookmark(dummyDetailNews)
        Mockito.verify(newsRepository).deleteNews(dummyDetailNews.title)
    }
}