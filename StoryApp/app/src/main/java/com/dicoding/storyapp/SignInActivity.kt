package com.dicoding.storyapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnSignUp.setOnClickListener(this)

        handleError()
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnSignUp){
            val i = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(i)
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
}