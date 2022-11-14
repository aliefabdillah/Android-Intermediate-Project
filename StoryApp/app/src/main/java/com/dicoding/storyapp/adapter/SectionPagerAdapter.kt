package com.dicoding.storyapp.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dicoding.storyapp.ui.ListStoryFragment
import com.dicoding.storyapp.ui.MapStoryFragment

class SectionPagerAdapter(activity: AppCompatActivity): FragmentStateAdapter(activity) {

    var token = ""

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when(position){
            0 -> {
                fragment = ListStoryFragment()
                fragment.arguments = Bundle().apply {
                    putString(ListStoryFragment.TOKEN, token)
                }
            }
            1 -> {
                fragment = MapStoryFragment()
                fragment.arguments = Bundle().apply {
                    putString(MapStoryFragment.TOKEN, token)
                }
            }
        }

        return fragment as Fragment
    }
}