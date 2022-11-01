package com.dicoding.storyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.local.UserModel
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.databinding.ActivitySignInBinding
import com.dicoding.storyapp.models.LoginViewModel
import com.dicoding.storyapp.models.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SignInActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnSignUp.setOnClickListener(this)
        binding.btnSignIn.setOnClickListener(this)

        setupViewModel()
        handleError()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnSignUp -> {
                val i = Intent(this@SignInActivity, SignUpActivity::class.java)
                startActivity(i)
            }
            R.id.btnSignIn -> {
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                loginViewModel.signInUser(email, password)
            }
        }
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.isLoading.observe(this@SignInActivity){
            showLoading(it)
        }

        loginViewModel.error.observe(this@SignInActivity){ error ->
            signInCallback(error)
        }
    }

    private fun signInCallback(error: Boolean){
        if (error){
            loginViewModel.toastText.observe(this@SignInActivity){
                it.getContentIfNotHandled()?.let { toastText ->
                    Toast.makeText(this@SignInActivity, toastText, Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            loginViewModel.userData.observe(this){ dataLogin ->
                val username = dataLogin.name
                val userToken = dataLogin.token
                loginViewModel.saveUser(UserModel(username, userToken, true))
                AlertDialog.Builder(this).apply {
                    setMessage(getString(R.string.successfully_login_message))
                    setPositiveButton(getString(R.string.next)) { _, _ ->
                        val intent = Intent(context, ListStoryActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    create()
                    show()
                }
            }
        }
    }

    private fun handleError() {
        binding.btnSignIn.isEnabled = false
        binding.emailEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(char: Editable) {
                if (!isEmailValid(char.toString())){
                    binding.emailEditText.setBackgroundResource(R.drawable.border_input_error)
                    binding.emailHandler.alpha = 1f
                    binding.emailHandler.text = getString(R.string.email_error)
                    binding.btnSignIn.isEnabled = false
                }else{
                    binding.emailEditText.setBackgroundResource(R.drawable.border_input)
                    binding.emailHandler.alpha = 0f
                    if (binding.passwordEditText.text?.isNotEmpty() == true){
                        binding.btnSignIn.isEnabled = true
                    }
                }
            }

            private fun isEmailValid(email: CharSequence): Boolean{
                return Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }

        })

        binding.passwordEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length < 6){
                    binding.passwordEditText.setBackgroundResource(R.drawable.border_input_error)
                    binding.passwordHandler.alpha = 1f
                    binding.btnSignIn.isEnabled = false
                }else{
                    binding.passwordEditText.setBackgroundResource(R.drawable.border_input)
                    binding.passwordHandler.alpha = 0f
                    if (binding.emailEditText.text?.isNotEmpty() == true){
                        binding.btnSignIn.isEnabled = true
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //
            }

        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingIcon.visibility = View.VISIBLE
        } else {
            binding.loadingIcon.visibility = View.GONE
        }
    }
}