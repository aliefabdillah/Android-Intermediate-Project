package com.dicoding.storyapp.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.api.ListStoryItem
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.databinding.ActivityDetailStoryBinding
import com.dicoding.storyapp.models.MainViewModel
import com.dicoding.storyapp.models.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var detailViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
    }

    private fun setupViewModel() {
        detailViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        val token = intent.getStringExtra("TOKEN")
        val id = intent.getStringExtra("ID")
        println(id)
        if (token != null && id != null) {
            detailViewModel.getDetailStory(token, id)
        }

        detailViewModel.detailStory.observe(this){ detailStory ->
            setupView(detailStory)
        }
    }

    private fun setupView(detailStory: ListStoryItem) {

        title = getString(R.string.story_name, detailStory.name)

        //add data to view
        Glide.with(this@DetailStoryActivity)
            .load(detailStory.photoUrl)
            .into(binding.imageDetailView)

        binding.tvUsername.text = detailStory.name
        binding.tvDescription.text = detailStory.description
    }
}