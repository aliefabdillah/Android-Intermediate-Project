package com.dicoding.storyapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.FragmentMapStoryBinding

class MapStoryFragment : Fragment() {
    private lateinit var binding: FragmentMapStoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = arguments?.getString(TOKEN)
    }

    companion object{
        const val TOKEN = ""
    }
}