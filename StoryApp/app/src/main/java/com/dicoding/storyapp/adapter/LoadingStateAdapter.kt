package com.dicoding.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.storyapp.databinding.ItemLoadingBinding

class LoadingStateAdapter(private val retry: () -> Unit): LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {

    class LoadingStateViewHolder(private val binding: ItemLoadingBinding, retry: () -> Unit):
        RecyclerView.ViewHolder(binding.root){
            init {
                binding.retryButton.setOnClickListener { retry.invoke() }
            }

            fun bind(loadState: LoadState){
                with(binding){
                    progressBar.isVisible = loadState is LoadState.Loading
                    retryButton.isVisible = loadState is LoadState.Error
                    progressBar.isVisible = loadState is LoadState.Error
                    errorMsg.isVisible = !(loadState as? LoadState.Error)?.error?.message.isNullOrBlank()
                    errorMsg.text = (loadState as? LoadState.Error)?.error?.message
                }
            }
        }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, retry)
    }
}