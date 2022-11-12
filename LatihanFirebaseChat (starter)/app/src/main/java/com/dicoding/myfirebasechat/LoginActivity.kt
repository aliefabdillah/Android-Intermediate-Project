package com.dicoding.myfirebasechat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.myfirebasechat.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding:  ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //config google sign in
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)               //metode login untuk aplikasi biasa
            .requestIdToken(getString(R.string.default_web_client_id))  //resource string otomatis dari google-service.json
            .requestEmail()                                             //digunakan untuk menentukan data spesifik yang ingin diambil
            .build()                                                    //

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //inisialisasi firebase Auth
        auth = Firebase.auth

        binding.signInButton.setOnClickListener { signIn() }
    }

    private fun signIn() {
        //intent sign in
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    //sign in untuk mendapatkan pop up berubah akun google
    private var resultLauncher = registerForActivityResult(StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                //google sign in was successfull, authenticate with firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)               //jika sudah login mendapatkan idToken
            }catch (e: ApiException){
                Log.w(TAG, "Google Sign In Failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)    //mendapatkan credential dari idToken yang sudah didapat sebelumnya
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful){
                    //sign in success, update ui with the signed in users information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser                         //user dalam bentuk FirebaseUser
                    updateUI(user)
                }else{
                    //if sign in fails display message
                    Log.w(TAG, "signInWithCredential:Failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        //jika user tidak null
        if (user != null){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        //check if user is sign in (non-null) and update ui
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}