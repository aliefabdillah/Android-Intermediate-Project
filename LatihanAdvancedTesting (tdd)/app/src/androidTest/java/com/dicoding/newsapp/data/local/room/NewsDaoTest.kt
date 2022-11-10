package com.dicoding.newsapp.data.local.room
/*
Scenario:
a. Ketika menyimpan data berhasil
Sama dengan data ketika getBookmarkedNews.
isNewsBookmarked bernilai true.

b. Ketika menghapus data berhasil
Data getBookmarkedNews kosong.
isNewsBookmarked bernilai false.


* */
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.dicoding.newsapp.utils.DataDummy
import com.dicoding.newsapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)      // merupakan annotation yang digunakan untuk menjalankan testing menggunakan AndroidX Test
@SmallTest              //gunakan untuk menandai instrumentation test yang bersifat kecil. Ini digunakan untuk membantu menentukan jenis test mana yang ingin dijalankan.
class NewsDaoTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: NewsDatabase
    private lateinit var dao: NewsDao
    private val sampleNews = DataDummy.generateDummyNewsEntity()[0]

    @Before
    fun initDb(){
        //membuat database dengan inMemoryDatabase yang merupakan db dengan sifat sementara
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NewsDatabase::class.java
        ).build()
        dao = database.newsDao()
    }

    @After
    fun closeDb() = database.close()

    /*
    runTest di sini tidak perlu menggunakan MainDispatcherRule seperti pada local test. Hal ini
    karena pada instrumentation testing, kita bisa menjalankan test dengan menggunakan Dispatcher.Main.
    * */
    @Test
    fun saveNews_Success() = runBlockingTest {
        dao.saveNews(sampleNews)
        val actualNews = dao.getBookmarkedNews().getOrAwaitValue()
        Assert.assertEquals(sampleNews.title, actualNews[0].title)
        Assert.assertTrue(dao.isNewsBookmarked(sampleNews.title).getOrAwaitValue())
    }

    @Test
    fun deleteNews_Success() = runBlockingTest {
        dao.saveNews(sampleNews)
        dao.deleteNews(sampleNews.title)
        val actualNews = dao.getBookmarkedNews().getOrAwaitValue()
        Assert.assertTrue(actualNews.isEmpty())
        Assert.assertFalse(dao.isNewsBookmarked(sampleNews.title).getOrAwaitValue())
    }

    /*
    Catatan:
    Untuk instrumentation testing, kita tidak bisa menggunakan backtick untuk nama fungsi sebagaimana
    pada local test.
    * */
}