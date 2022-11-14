package com.dicoding.storyapp.ui

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.ListStoryAdapter
import com.dicoding.storyapp.data.local.StoryEntity
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.databinding.ActivityListStoryBinding
import com.dicoding.storyapp.databinding.ItemRowStoryBinding
import com.dicoding.storyapp.models.DbViewModel
import com.dicoding.storyapp.models.DbViewModelFactory
import com.dicoding.storyapp.models.MainViewModel
import com.dicoding.storyapp.models.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ListStoryActivity : AppCompatActivity(), View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: ActivityListStoryBinding
    private lateinit var itemBinding: ItemRowStoryBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        itemBinding = ItemRowStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        binding.fabAdd.setOnClickListener(this)
        binding.swipeRefresh.setColorSchemeResources(R.color.orange_200)
        binding.swipeRefresh.setOnRefreshListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.fab_add){
            startActivity(Intent(this@ListStoryActivity, UploadActivity::class.java))
        }
    }

    override fun onRefresh() {
        setupViewModel()
        Handler().postDelayed({
            binding.swipeRefresh.isRefreshing = false
        }, REFRESH_TIME)
    }

    private fun setupViewModel() {
        val viewModelFactory: DbViewModelFactory = DbViewModelFactory.getInstance(this)
        val dbViewModel: DbViewModel by viewModels {
            viewModelFactory
        }

        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.isLoading.observe(this){
            showLoading(it)
        }

        mainViewModel.toastText.observe(this){
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(this@ListStoryActivity, toastText, Toast.LENGTH_SHORT).show()
            }
        }

        mainViewModel.getUser().observe(this){ user ->
            if (user.isLogin){
                TOKEN = user.token
                title = getString(R.string.welcome, user.name)
                dbViewModel.deleteAllData()
                mainViewModel.getListStories(user.token)
            }else{
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }

        mainViewModel.storiesData.observe(this){ listStories ->
            listStories.forEach {
                dbViewModel.saveStoryToDb(it)
            }
        }

        dbViewModel.getStories().observe(this){ listStories ->
            showResult(listStories)
        }
    }

    private fun showResult(listStories: List<StoryEntity>) {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        binding.rvStory.setHasFixedSize(true)

        val adapter = ListStoryAdapter(listStories)
        if (adapter.itemCount == 0){
            binding.tvEmptyListStory.alpha = 1f
        }else{
            binding.tvEmptyListStory.alpha = 0f
            binding.rvStory.adapter = adapter
            adapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback{
                override fun onItemClicked(data: StoryEntity) {
                    val iToDetail = Intent(this@ListStoryActivity, DetailStoryActivity::class.java)
                    iToDetail.putExtra("ID", data.id)
                    iToDetail.putExtra("TOKEN", TOKEN)
                    val options = ActivityOptions.makeSceneTransitionAnimation(this@ListStoryActivity,
                            Pair(itemBinding.imgItem, "storyImage"),
                            Pair(itemBinding.tvUsername, "username")
                        )
                    startActivity(iToDetail, options.toBundle())
                }
            })
        }

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingIcon.visibility = View.VISIBLE
        } else {
            binding.loadingIcon.visibility = View.GONE
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
        private const val REFRESH_TIME = 2000L
        private var TOKEN = ""
    }
}