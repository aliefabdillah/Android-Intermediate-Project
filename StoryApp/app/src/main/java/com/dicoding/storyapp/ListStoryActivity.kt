package com.dicoding.storyapp

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.local.UserModel
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.databinding.ActivityListStoryBinding
import com.dicoding.storyapp.models.MainViewModel
import com.dicoding.storyapp.models.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ListStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListStoryBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this){ user ->
            if (user.isLogin){
                title = getString(R.string.welcome, user.name)
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
        if (item.itemId == R.id.logoutMenu){
            AlertDialog.Builder(this).apply {
                setTitle(R.string.dialogSignOutTitle)
                setMessage(R.string.dialogSignOutMessage)
                setPositiveButton(getString(R.string.sign_out)) { _, _ ->
                    mainViewModel.logout()
                }
                setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                create()
                show()
            }
        }
        return true
    }
}