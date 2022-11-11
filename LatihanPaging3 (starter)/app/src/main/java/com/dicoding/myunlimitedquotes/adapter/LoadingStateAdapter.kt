package com.dicoding.myunlimitedquotes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.myunlimitedquotes.databinding.ItemLoadingBinding

class LoadingStateAdapter(private val retry: () -> Unit): LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {
    class LoadingStateViewHolder(private val binding: ItemLoadingBinding, retry: () -> Unit):
        RecyclerView.ViewHolder(binding.root) {

        //inisialiasi berupa retry button dapat diakses di main activity karena menggunakan invoke
        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        //fungsi untuk mengatur state
        fun bind(loadState: LoadState){
            //jika loading state error
            if (loadState is  LoadState.Error){
                //menampilkan pesan error sesuai pesan dari loadstate
                binding.errorMsg.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading      //menampilkan animasi loading ketika state loading
            binding.retryButton.isVisible = loadState is LoadState.Error        //menampilkan tombol retry ketika state error
            binding.errorMsg.isVisible = loadState is LoadState.Error           //menampilkan pesan error ketika state error
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }




}