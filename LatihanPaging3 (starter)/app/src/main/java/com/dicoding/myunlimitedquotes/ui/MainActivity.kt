package com.dicoding.myunlimitedquotes.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.myunlimitedquotes.adapter.LoadingStateAdapter
import com.dicoding.myunlimitedquotes.adapter.QuoteListAdapter
import com.dicoding.myunlimitedquotes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvQuote.layoutManager = LinearLayoutManager(this)

        getData()
    }

    private fun getData() {
        val adapter = QuoteListAdapter()
//        binding.rvQuote.adapter = adapter
        //menambahkan load pada paging
        binding.rvQuote.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                //fungsi retry untuk mengulang load
                adapter.retry()
            }
        )
        //memasukan paging data ke dalam adapter
        mainViewModel.quote.observe(this){
            //apabila keluarnnya berupa live data harus menggunakan parameter lifecycle
            adapter.submitData(lifecycle, it)
        }

//        mainViewModel.getQuote()
//        mainViewModel.quote.observe(this, {
//            adapter.submitList(it)
//        })
    }
}