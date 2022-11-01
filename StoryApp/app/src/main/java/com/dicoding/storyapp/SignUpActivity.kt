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

        handleError()
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

    private fun handleError() {
        binding.btnSignUp.isEnabled = false
        binding.nameEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.nameHandler.alpha = 1f
            }

            override fun onTextChanged(s: CharSequence, start: Int, end: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    binding.nameHandler.alpha = 1f
                    binding.nameEditText.setBackgroundResource(R.drawable.border_input_error)
                    binding.btnSignUp.isEnabled = false
                }else{
                    binding.nameHandler.alpha = 0f
                    binding.nameEditText.setBackgroundResource(R.drawable.border_input)
                    if (binding.emailEditText.text?.isNotEmpty() == true && binding.passwordEditText.text?.isNotEmpty() == true){
                        binding.btnSignUp.isEnabled = true
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //
            }

        })

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
                    binding.btnSignUp.isEnabled = false
                }else{
                    binding.emailEditText.setBackgroundResource(R.drawable.border_input)
                    binding.emailHandler.alpha = 0f
                    if (binding.passwordEditText.text?.isNotEmpty() == true && binding.nameEditText.text?.isNotEmpty() == true){
                        binding.btnSignUp.isEnabled = true
                    }
                }
            }

            private fun isEmailValid(email: CharSequence): Boolean{
                return Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length < 6){
                    binding.passwordEditText.setBackgroundResource(R.drawable.border_input_error)
                    binding.passwordHandler.alpha = 1f
                    binding.btnSignUp.isEnabled = false
                }else{
                    binding.passwordEditText.setBackgroundResource(R.drawable.border_input)
                    binding.passwordHandler.alpha = 0f
                    if (binding.emailEditText.text?.isNotEmpty() == true && binding.nameEditText.text?.isNotEmpty() == true){
                        binding.btnSignUp.isEnabled = true
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //
            }
        })
    }

}