package com.dicoding.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.api.ListStoryItem
import com.dicoding.storyapp.databinding.ItemRowStoryBinding

class ListStoryAdapter(private val listStories: List<ListStoryItem>) :
    RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {

    class ListViewHolder(var binding: ItemRowStoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = listStories[position]
        holder.binding.tvUsername.text = story.name
        Glide.with(holder.itemView.context)
            .load(story.photoUrl)
            .into(holder.binding.imgItem)
    }

    override fun getItemCount(): Int = listStories.size

}