package com.dicoding.storyapp.ui

import android.app.ActivityOptions
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.ListStoryAdapter
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.local.StoryEntity
import com.dicoding.storyapp.databinding.ActivityListStoryBinding
import com.dicoding.storyapp.databinding.ItemRowStoryBinding
import com.dicoding.storyapp.models.DbViewModel
import com.dicoding.storyapp.models.DbViewModelFactory
import com.dicoding.storyapp.models.MainViewModel
import com.dicoding.storyapp.models.ViewModelFactory

class ListStoryActivity : AppCompatActivity(), View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener {
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
        binding.swipeRefresh.setColorSchemeResources(R.color.orange_200)
        binding.swipeRefresh.setOnRefreshListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.fab_add){
            startActivity(Intent(this@ListStoryActivity, UploadActivity::class.java))
        }
    }

    override fun onRefresh() {
        setupData()
        Handler().postDelayed({
            binding.swipeRefresh.isRefreshing = false
        }, REFRESH_TIME)
    }

    private fun setupData() {
//        val viewModelFactory: DbViewModelFactory = DbViewModelFactory.getInstance(this)
//        val dbViewModel: DbViewModel by viewModels {
//            viewModelFactory
//        }

        mainViewModel.getUser().observe(this){ user ->
            if (user.isLogin){
                TOKEN = user.token
                title = getString(R.string.welcome, user.name)
                dbViewModel.deleteAllData()
                getStoryCallback(user.token)
            }else{
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }

        /*mainViewModel = ViewModelProvider(
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
        }*/

        dbViewModel.getStories().observe(this){ listStories ->
            showResult(listStories)
        }
    }

    private fun getStoryCallback(token: String) {
        mainViewModel.getListStories(token).observe(this){ result ->
            if (result != null){
                when(result){
                    is Result.Loading -> binding.loadingIcon.visibility = View.VISIBLE
                    is Result.Success -> {
                        if (result.data.isEmpty()){
                            Toast.makeText(this, getString(R.string.list_story_is_empty), Toast.LENGTH_LONG).show()
                        }else{
                            binding.loadingIcon.visibility = View.GONE
                            result.data.forEach {
                                dbViewModel.saveStoryToDb(it)
                            }
                        }
                    }
                    is Result.Error -> {
                        binding.loadingIcon.visibility = View.GONE
                        result.error.getContentIfNotHandled()?.let { toastText ->
                            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
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