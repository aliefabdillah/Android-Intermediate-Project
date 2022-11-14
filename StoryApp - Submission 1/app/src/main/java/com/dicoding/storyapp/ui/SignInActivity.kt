package com.dicoding.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.local.UserModel
import com.dicoding.storyapp.databinding.ActivitySignInBinding
import com.dicoding.storyapp.models.LoginViewModel
import com.dicoding.storyapp.models.ViewModelFactory

class SignInActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySignInBinding
    private val loginViewModel: LoginViewModel by viewModels { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnSignUp.setOnClickListener(this)
        binding.btnSignIn.setOnClickListener(this)

        setupAnimation()
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
                signInCallback(email, password)
            }
        }
    }

    private fun setupViewModel() {
//        loginViewModel = ViewModelProvider(
//            this,
//            ViewModelFactory(UserPreference.getInstance(dataStore))
//        )[LoginViewModel::class.java]
//
//        loginViewModel.isLoading.observe(this@SignInActivity){
//            showLoading(it)
//        }
//
//        loginViewModel.error.observe(this@SignInActivity){ error ->
//            signInCallback(error)
//        }
    }

    private fun signInCallback(email: String, password: String){
        loginViewModel.signInUser(email, password).observe(this){ result ->
            if (result != null){
                when(result){
                    is Result.Loading -> {
                        binding.loadingIcon.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.loadingIcon.visibility = View.GONE
                        val userName = result.data.name
                        val userToken = result.data.token
                        loginViewModel.saveUser(UserModel(userName, userToken, true))
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
                    is Result.Error -> {
                        binding.loadingIcon.visibility = View.GONE
                        result.error.getContentIfNotHandled()?.let {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
/*        if (error){
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
        }*/
    }

    private fun setupAnimation() {
        val imageIcon = setOtherViewAnimation(binding.storyAppIcon)
        val signInTV = setOtherViewAnimation(binding.LoginText)
        val emailET = setOtherViewAnimation(binding.emailEditText)
        val passET = setOtherViewAnimation(binding.passwordEditText)
        val signInBTN = setOtherViewAnimation(binding.btnSignIn)
        val signUpBTN = setOtherViewAnimation(binding.btnSignUp)

        AnimatorSet().apply {
            playSequentially(imageIcon, signInTV, emailET, passET, signInBTN, signUpBTN)
            start()
        }
    }

    private fun setOtherViewAnimation(view: View): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, View.ALPHA, 1f).apply {
            duration = 500
        }
    }
}