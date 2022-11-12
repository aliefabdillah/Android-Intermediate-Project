package com.dicoding.myfirebasechat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.myfirebasechat.databinding.ActivityMainBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: FirebaseMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configFirebase()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_menu -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    private fun configFirebase(){
        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        if (firebaseUser == null){
            //user not login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        //insert database
        db = Firebase.database
        //menentukan lokasi data dengan menggunakan fungsi child
        val messageRef = db.reference.child(MESSAGES_CHILD)

        buttonListener(firebaseUser, messageRef)
        chatLayoutConfig(firebaseUser, messageRef)
    }

    private fun chatLayoutConfig(firebaseUser: FirebaseUser, messageRef: DatabaseReference) {
        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = manager

        //membuat query mengambil data message dari firebase database
        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(messageRef, Message::class.java)
            .build()

        //infalte adapter dengan parameter tambahan berupa current username
        adapter = FirebaseMessageAdapter(options, firebaseUser.displayName)
        binding.messageRecyclerView.adapter = adapter
    }

    private fun buttonListener(firebaseUser: FirebaseUser, messageRef: DatabaseReference) {
        binding.sendButton.setOnClickListener {
            val friendMessage = Message(
                binding.messageEditText.text.toString(),
                firebaseUser.displayName.toString(),
                firebaseUser.photoUrl.toString(),
                Date().time
            )

            //setvalue digunakan untuk merubah dan mengganti data
            messageRef.push().setValue(friendMessage) { error, _ ->
                if (error != null){
                    Toast.makeText(this, getString(R.string.send_error) + error.message, Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this, getString(R.string.send_success), Toast.LENGTH_SHORT).show()
                }
            }

            binding.messageEditText.setText("")
        }
    }

    private fun signOut() {
        //sign out method menggunakan firebase
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    companion object {
        const val MESSAGES_CHILD = "messages"
    }

}