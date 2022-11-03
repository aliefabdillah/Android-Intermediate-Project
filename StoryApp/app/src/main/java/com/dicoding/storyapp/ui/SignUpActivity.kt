package com.dicoding.storyapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivitySignUpBinding
import com.dicoding.storyapp.models.SignUpViewModel

class SignUpActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnSignUp.setOnClickListener(this)

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