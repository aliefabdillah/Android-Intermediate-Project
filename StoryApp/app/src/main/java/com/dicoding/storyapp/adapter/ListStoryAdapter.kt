package com.dicoding.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.local.StoryEntity
import com.dicoding.storyapp.databinding.ItemRowStoryBinding

class ListStoryAdapter(private val listStories: List<StoryEntity>) :
    RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {

    class ListViewHolder(var binding: ItemRowStoryBinding): RecyclerView.ViewHolder(binding.root)

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = listStories[position]
        holder.binding.tvUsername.text = story.username
        Glide.with(holder.itemView.context)
            .load(story.photoUrl)
            .into(holder.binding.imgItem)

        holder.binding.cardView.setOnClickListener {
            onItemClickCallback.onItemClicked(listStories[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listStories.size

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryEntity)
    }
}