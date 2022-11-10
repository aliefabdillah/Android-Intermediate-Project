package com.dicoding.newsapp.data
/*
Scnario:
Untuk membuat NewsRepository, kita membutuhkan ApiService dan NewsDao. Sebenarnya Anda bisa saja
menggunakan mock untuk mengatasi hal tersebut, tetapi pada latihan ini kita akan menggunakan fake
untuk menambah wawasan Anda.

Konsep dasar ketika membuat Fake yaitu object tiruan tersebut haruslah dibuat interface-nya terlebih
dahulu. Untungnya, kita bisa langsung membuat object tiruannya karena ApiService dan NewsDao sudah
merupakan interface.

a. Ketika mengambil data dari internet
Memastikan data tidak null.
Memastikan jumlah data sesuai dengan yang diharapkan.

b. Ketika menyimpan data ke database
Data tersebut muncul di getBookmarkedNews.
Fungsi isBookmarked bernilai True.

c. Ketika menghapus data dari database
Data tersebut tidak muncul di getBookmarkedNews.
Fungsi isBookmarked bernilai False.
* */
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dicoding.newsapp.data.local.room.NewsDao
import com.dicoding.newsapp.data.remote.retrofit.ApiService
import com.dicoding.newsapp.utils.DataDummy
import com.dicoding.newsapp.utils.MainDispatcherRule
import com.dicoding.newsapp.utils.getOrAwaitValue
import com.dicoding.newsapp.utils.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NewsRepositoryTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var apiService: ApiService
    private lateinit var newsDao : NewsDao
    private lateinit var newsRepository: NewsRepository

    //instance variabel DAO dan ApiService
    @Before
    fun setUp() {
        apiService = FakeApiService()
        newsDao = FakeNewsDao()
        newsRepository = NewsRepository(apiService, newsDao)
    }

    //testing mengambil data dari API dan hasilnya tidak null
    @Test
    fun `when getHeadlineNews Should Not Null`() = runTest {
        val expectedNews = DataDummy.generateDummyNewsResponse()
        val actualNews = newsRepository.getHeadlineNews()

        /*
        * disini menggunakan observeForTesting karena data yang diemit pada fungsi tersebut
        * lebih dari satu*/
        actualNews.observeForTesting {
            Assert.assertNotNull(actualNews)
            Assert.assertEquals(
                expectedNews.articles.size,
                (actualNews.value as Result.Success).data.size
            )
        }
    }

    //testing ketika menyimpan news dan tampil ketika memanggil getBookmarkedNews()
    @Test
    fun `when saveNews Should Exist in getBookmarkedNews`() = runTest {
        val sampleNews = DataDummy.generateDummyNewsEntity()[0]
        newsDao.saveNews(sampleNews)
        val actualNews = newsRepository.getBookmarkedNews().getOrAwaitValue()
        Assert.assertTrue(actualNews.contains(sampleNews))
        Assert.assertTrue(newsRepository.isNewsBookmarked(sampleNews.title).getOrAwaitValue())
    }

    //testing untuk menghapus news dari db dan memastikan tidak tampil di getBookmarkedNews()
    @Test
    fun `when deleteNews Should Not Exist in getBookmarkedNews`() = runTest {
        val sampleNews = DataDummy.generateDummyNewsEntity()[0]
        newsRepository.saveNews(sampleNews)
        newsRepository.deleteNews(sampleNews.title)
        val actualNews = newsRepository.getBookmarkedNews().getOrAwaitValue()
        Assert.assertFalse(actualNews.contains(sampleNews))
        Assert.assertFalse(newsRepository.isNewsBookmarked(sampleNews.title).getOrAwaitValue())
    }
}