package com.dicoding.storyapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.api.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityDetailStoryBinding
import com.dicoding.storyapp.models.DetailViewModel
import com.dicoding.storyapp.modelsfactory.ViewModelFactory

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private val detailViewModel: DetailViewModel by viewModels { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
    }

    private fun setupViewModel() {

        val token = intent.getStringExtra("TOKEN")
        val id = intent.getStringExtra("ID")

        if (token != null && id != null) {
            detailViewModel.getDetailStory(token, id).observe(this){ result ->
                if (result != null){
                    when(result){
                        is Result.Loading -> binding.loadingIcon.visibility = View.VISIBLE
                        is Result.Success -> {
                            binding.loadingIcon.visibility = View.GONE
                            setupView(result.data)
                        }
                        is Result.Error -> {
                            binding.loadingIcon.visibility = View.GONE
                            result.error.getContentIfNotHandled()?.let { toastText ->
                                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
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