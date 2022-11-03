package com.dicoding.storyapp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.databinding.ActivitySignUpBinding
import com.dicoding.storyapp.models.SignUpViewModel
import com.dicoding.storyapp.models.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SignUpActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnSignUp.setOnClickListener(this)

        setupViewModel()

        signUpViewModel.isLoading.observe(this@SignUpActivity){
            showLoading(it)
        }

        signUpViewModel.error.observe(this@SignUpActivity){ error ->
            signUpCallback(error)
        }

    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnSignUp){
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val pass = binding.passwordEditText.text.toString()
            signUpViewModel.signUpUser(name, email, pass)
            println("$name, $email, $pass")
        }
    }

    private fun setupViewModel() {
        signUpViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[SignUpViewModel::class.java]
    }

    private fun signUpCallback(error: Boolean) {
        if (error){
            signUpViewModel.toastText.observe(this@SignUpActivity){
                it.getContentIfNotHandled()?.let { toastText ->
                    Toast.makeText(this@SignUpActivity, toastText, Toast.LENGTH_LONG).show()
                }
            }
        }else{
            signUpViewModel.toastText.observe(this@SignUpActivity){
                it.getContentIfNotHandled()?.let { toastText ->
                    Toast.makeText(this@SignUpActivity, toastText, Toast.LENGTH_LONG).show()
                }
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingIcon.visibility = View.VISIBLE
        } else {
            binding.loadingIcon.visibility = View.GONE
        }
    }
}