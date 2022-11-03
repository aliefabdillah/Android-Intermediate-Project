package com.dicoding.storyapp

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.api.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {


        val detailsUser: ListStoryItem? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRADATA, ListStoryItem::class.java)
        } else {
            intent.getParcelableExtra<ListStoryItem>(EXTRADATA) as ListStoryItem
        }

        title = getString(R.string.story_name, detailsUser?.name)

        //add data to view
        Glide.with(this@DetailStoryActivity)
            .load(detailsUser?.photoUrl)
            .into(binding.imageDetailView)

        binding.tvUsername.text = detailsUser?.name
        binding.tvDescription.text = detailsUser?.description
    }

    companion object{
        const val EXTRADATA = ""
    }
}