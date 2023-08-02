package com.chetna.authpro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.chetna.authpro.MainActivity.Companion.auth
import com.chetna.authpro.databinding.ActivityLogin2Binding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogin2Binding
    private lateinit var number:String
    private lateinit var phoneNumber:TextInputLayout
    private lateinit var signUp: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityLogin2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        binding= ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        phoneNumber=findViewById(R.id.phoneNumber)
        signUp=findViewById(R.id.button3)
        binding.button4.setOnClickListener {
            startActivity(Intent(this,PhoneActivity::class.java))
            finish()
        }

        signUp.setOnClickListener {
            number = phoneNumber.editText?.text?.toString() ?: ""
            if(number.isEmpty()||binding.userName.text.toString().isEmpty()){
                Toast.makeText(this,"Please Fill All The Details",Toast.LENGTH_SHORT).show()
            }
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
                Toast.makeText(this,"Please Enter Number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this,"Authentication Successful", Toast.LENGTH_SHORT).show()
                    sendToMain()
                } else {
                    // Sign in failed, display a message and update the UI
                    Toast.makeText(this,"Authentication Failed", Toast.LENGTH_SHORT).show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun init(){

        phoneNumber=findViewById(R.id.phoneNumber)
        signUp=findViewById(R.id.button3)
        //progress=findViewById(R.id.phoneProgressBar)
        auth= FirebaseAuth.getInstance()
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
            val intent=Intent(this@LoginActivity,OTPActivity::class.java)
            intent.putExtra("OTP",verificationId)
            intent.putExtra("resendToken",token)
            intent.putExtra("phoneNumber",number)
            startActivity(intent)
            //  progress.visibility=View.INVISIBLE
        }
    }
}