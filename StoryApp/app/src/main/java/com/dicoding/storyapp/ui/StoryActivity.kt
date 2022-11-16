package com.dicoding.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityListStoryBinding
import com.dicoding.storyapp.databinding.ItemRowStoryBinding
import com.dicoding.storyapp.models.DbViewModel
import com.dicoding.storyapp.models.DbViewModelFactory
import com.dicoding.storyapp.models.MainViewModel
import com.dicoding.storyapp.models.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class StoryActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityListStoryBinding
    private lateinit var itemBinding: ItemRowStoryBinding

    private var token = ""

    private val mainViewModel: MainViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private val dbViewModel: DbViewModel by viewModels { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        itemBinding = ItemRowStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        when(item.itemId){
            R.id.listView -> {
                fragment = ListStoryFragment()
                fragment.arguments = Bundle().apply {
                    putString(ListStoryFragment.TOKEN, token)
                }
            }
            R.id.mapsView -> {
                fragment = MapStoryFragment()
                fragment.arguments = Bundle().apply {
                    putString(MapStoryFragment.TOKEN, token)
                }
            }
        }

        if (fragment != null) {
            renderFragment(fragment)
        }

        return true
    }

    private fun renderFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.flFragment, fragment).commit()
    }


    private fun setupData() {

        mainViewModel.getUser().observe(this){ user ->
            if (user.isLogin){
                token = user.token
                title = getString(R.string.welcome, user.name)
                dbViewModel.deleteAllData()
                binding.bottomNavigationView.selectedItemId = R.id.listView
            }else{
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }
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
            R.id.addStoryBtn -> {
                startActivity(Intent(this@StoryActivity, UploadActivity::class.java))
            }
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

    companion object
}