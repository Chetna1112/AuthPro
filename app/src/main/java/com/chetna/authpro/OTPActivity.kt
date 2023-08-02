package com.chetna.authpro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.chetna.authpro.MainActivity.Companion.auth
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var verifyBtn:Button
    private lateinit var resend:TextView
    private lateinit var intpu1:EditText
    private lateinit var intpu2:EditText
    private lateinit var intpu3:EditText
    private lateinit var intpu4:EditText
    private lateinit var intpu5:EditText
    private lateinit var intpu6:EditText
    private lateinit var OTP:String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber:String
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)

        OTP=intent.getStringExtra("OTP").toString()
        resendToken= intent.getParcelableExtra("resendToken")!!
        phoneNumber=intent.getStringExtra("phoneNumber")!!
        init()
        progressBar.visibility=View.INVISIBLE
        addTextChangedLisstener()

        resend.setOnClickListener {
            resendVerificationCode()
        }

        verifyBtn.setOnClickListener {
            val typeOTP=intpu1.text.toString()+intpu2.text.toString()+
                        intpu3.text.toString()+intpu4.text.toString()+
                        intpu5.text.toString()+intpu6.text.toString()
            if(typeOTP.isNotEmpty()){
                if(typeOTP.length==6){
                    val credential:PhoneAuthCredential=PhoneAuthProvider.getCredential(
                        OTP,typeOTP
                    )
                    progressBar.visibility=View.VISIBLE
                    signInWithPhoneAuthCredential(credential)
                    intent=Intent(this,MainActivity::class.java)
                    intent.putExtra("user","User Logged In")

                }else{
                    Toast.makeText(this,"Please Enter Correct OTP",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Please Enter OTP",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    progressBar.visibility=View.VISIBLE
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

    private fun sendToMain(){
        intent=Intent(this,MainActivity::class.java)
        intent.putExtra("user","Already Logged In")
        startActivity(intent)
    }

    private  fun addTextChangedLisstener(){
        intpu1.addTextChangedListener(EditTextWatcher(intpu1))
        intpu2.addTextChangedListener(EditTextWatcher(intpu2))
        intpu3.addTextChangedListener(EditTextWatcher(intpu3))
        intpu4.addTextChangedListener(EditTextWatcher(intpu4))
        intpu5.addTextChangedListener(EditTextWatcher(intpu5))
        intpu6.addTextChangedListener(EditTextWatcher(intpu6))


    }

    private fun init(){
        auth= FirebaseAuth.getInstance()
        progressBar=findViewById(R.id.otpProgressBar)
        verifyBtn=findViewById(R.id.verifyOTPBtn)
        resend=findViewById(R.id.resendTextView)
        intpu1=findViewById(R.id.otpEditText1)
        intpu2=findViewById(R.id.otpEditText2)
        intpu3=findViewById(R.id.otpEditText3)
        intpu4=findViewById(R.id.otpEditText4)
        intpu5=findViewById(R.id.otpEditText5)
        intpu6=findViewById(R.id.otpEditText6)
    }

    inner class EditTextWatcher(private val view:View):TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
           val text=s.toString()
            when(view.id) {
                R.id.otpEditText1 -> if (text.length == 1) intpu2.requestFocus()
                R.id.otpEditText2 -> if (text.length == 1) intpu3.requestFocus() else if (text.isEmpty()) intpu1.requestFocus()
                R.id.otpEditText3 -> if (text.length == 1) intpu4.requestFocus() else if (text.isEmpty()) intpu2.requestFocus()
                R.id.otpEditText4 -> if (text.length == 1) intpu5.requestFocus() else if (text.isEmpty()) intpu3.requestFocus()
                R.id.otpEditText5 -> if (text.length == 1) intpu6.requestFocus() else if (text.isEmpty()) intpu4.requestFocus()
                R.id.otpEditText6 -> if (text.isEmpty()) intpu6.requestFocus()
            }

        }

    }

    private fun resendVerificationCode(){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken )// OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
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
            OTP=verificationId
            resendToken=token
        }
    }

}