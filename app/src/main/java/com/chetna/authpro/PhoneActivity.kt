package com.chetna.authpro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.chetna.authpro.MainActivity.Companion.auth
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class PhoneActivity : AppCompatActivity() {
    private lateinit var sendOTP:Button
    private lateinit var phoneNumber:EditText
    private lateinit var auth:FirebaseAuth
    private lateinit var number:String
    private lateinit var signUp:Button
   // private lateinit var progress:ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)
        init()
        signUp.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        sendOTP.setOnClickListener {
            number=phoneNumber.text.trim().toString()
                if(number.isNotEmpty()) {
                     if (number.length == 10) {
                    number="+91$number"
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number) // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this) // Activity (for callback binding)
                        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                    //startActivity(Intent(this,OTPActivity::class.java))
                } else {
                    Toast.makeText(this, "Please Enter Correct Number", Toast.LENGTH_SHORT).show()
                }
            }
                else{
                    Toast.makeText(this,"Please Enter Number",Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this,"Authentication Successful",Toast.LENGTH_SHORT).show()
                    sendToMain()
                } else {
                    // Sign in failed, display a message and update the UI
                    Toast.makeText(this,"Authentication Failed",Toast.LENGTH_SHORT).show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun init(){
        sendOTP=findViewById(R.id.sendOTP)
        phoneNumber=findViewById(R.id.phoneEditTextNumber)
        signUp=findViewById(R.id.button4)
        //progress=findViewById(R.id.phoneProgressBar)
        auth=FirebaseAuth.getInstance()
    }

    private fun sendToMain(){
        startActivity(Intent(this,MainActivity::class.java))
    }
   val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.

            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.


            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }
            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID. Log.d(TAG, "onCodeSent:$verificationId")
            // Save verification ID and resending token so we can use them later
            val intent=Intent(this@PhoneActivity,OTPActivity::class.java)
            intent.putExtra("OTP",verificationId)
            intent.putExtra("resendToken",token)
            intent.putExtra("phoneNumber",number)
            startActivity(intent)
          //  progress.visibility=View.INVISIBLE
        }
    }

    /*verride fun onStart() {
        super.onStart()
        *//*if(auth.currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
        }*//*
    }*/
}