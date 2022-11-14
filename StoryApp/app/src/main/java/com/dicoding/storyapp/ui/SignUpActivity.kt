package com.dicoding.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.databinding.ActivitySignUpBinding
import com.dicoding.storyapp.models.SignUpViewModel
import com.dicoding.storyapp.models.ViewModelFactory

class SignUpActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnSignUp.setOnClickListener(this)

        setupAnimation()
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnSignUp){
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val pass = binding.passwordEditText.text.toString()
            signUpCallback(name, email, pass)
        }
    }

    private fun setupAnimation() {
        val imageIcon = setOtherViewAnimation(binding.storyAppIcon)
        val signUpTV = setOtherViewAnimation(binding.signUpText)
        val nameET = setOtherViewAnimation(binding.nameEditText)
        val emailET = setOtherViewAnimation(binding.emailEditText)
        val passET = setOtherViewAnimation(binding.passwordEditText)
        val signUpBTN = setOtherViewAnimation(binding.btnSignUp)

        AnimatorSet().apply {
            playSequentially(imageIcon, signUpTV, nameET, emailET, passET, signUpBTN)
            start()
        }
    }

    private fun setOtherViewAnimation(view: View): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, View.ALPHA, 1f).apply {
            duration = 500
        }
    }

    private fun signUpCallback(name: String, email: String, pass: String) {
        signUpViewModel.signUpUser(name, email, pass).observe(this){ result ->
            if (result != null){
                when(result){
                    is Result.Loading -> {
                        binding.loadingIcon.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.loadingIcon.visibility = View.GONE
                        Toast.makeText(this, result.data.message, Toast.LENGTH_LONG).show()
                        finish()
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
//        if (error){
//            signUpViewModel.toastText.observe(this@SignUpActivity){
//                it.getContentIfNotHandled()?.let { toastText ->
//                    Toast.makeText(this@SignUpActivity, toastText, Toast.LENGTH_LONG).show()
//                }
//            }
//        }else{
//            signUpViewModel.toastText.observe(this@SignUpActivity){
//                it.getContentIfNotHandled()?.let { toastText ->
//                    Toast.makeText(this@SignUpActivity, toastText, Toast.LENGTH_LONG).show()
//                }
//                finish()
//            }
//        }
    }
}