package com.chetna.authpro

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.chetna.authpro.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    companion object{
        lateinit var auth: FirebaseAuth
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth= FirebaseAuth.getInstance()
        /*if(auth.currentUser==null)
        {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }*/
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signIn.setOnClickListener{
            startActivity(Intent(this,PhoneActivity::class.java))
            finish()
        }
        binding.signOut.setOnClickListener {
           // auth.signOut()
            binding.userDetails.text="Not Signed In"
        }
    }

    override fun onResume() {
        super.onResume()
        updateData()
    }
//    val user = intent.getStringExtra("user")

    // Now you can use the "user" variable as needed

    private fun updateData(){
        val user = intent.getStringExtra("user")
        Log.d("abc",user.toString())
        if(user!=null){
            binding.userDetails.setText(user)
        }
    }

}