package com.dicoding.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.SectionPagerAdapter
import com.dicoding.storyapp.databinding.ActivityListStoryBinding
import com.dicoding.storyapp.databinding.ItemRowStoryBinding
import com.dicoding.storyapp.models.DbViewModel
import com.dicoding.storyapp.models.DbViewModelFactory
import com.dicoding.storyapp.models.MainViewModel
import com.dicoding.storyapp.models.ViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator

class StoryActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityListStoryBinding
    private lateinit var itemBinding: ItemRowStoryBinding

    private val mainViewModel: MainViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private val dbViewModel: DbViewModel by viewModels { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        itemBinding = ItemRowStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()

        binding.fabAdd.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.fab_add){
            startActivity(Intent(this@StoryActivity, UploadActivity::class.java))
        }
    }

    private fun setupData() {

        mainViewModel.getUser().observe(this){ user ->
            if (user.isLogin){
                TOKEN = user.token
                title = getString(R.string.welcome, user.name)
                dbViewModel.deleteAllData()
                createTabsLayout(user.token)
            }else{
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }
    }

    private fun createTabsLayout(token: String){
        val sectionPagerAdapter = SectionPagerAdapter(this@StoryActivity)
        sectionPagerAdapter.token = token
        binding.viewPager.adapter = sectionPagerAdapter

        TabLayoutMediator(binding.storyTab, binding.viewPager){ tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val viewModelFactory: DbViewModelFactory = DbViewModelFactory.getInstance(this)
        val dbViewModel: DbViewModel by viewModels {
            viewModelFactory
        }

        when(item.itemId){
            R.id.logoutMenu -> {
                AlertDialog.Builder(this).apply {
                    setTitle(R.string.dialogSignOutTitle)
                    setMessage(R.string.dialogSignOutMessage)
                    setPositiveButton(getString(R.string.sign_out)) { _, _ ->
                        mainViewModel.logout()
                        dbViewModel.deleteAllData()
                    }
                    setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.cancel()
                    }
                    create()
                    show()
                }
            }
            R.id.languageMenu -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }

        return true
    }

    companion object{
        private var TOKEN = ""

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.list,
            R.string.map
        )
    }
}