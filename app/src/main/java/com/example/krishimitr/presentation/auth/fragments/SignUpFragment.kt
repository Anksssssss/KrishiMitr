package com.example.krishimitr.presentation.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.krishimitr.MainActivity
import com.example.krishimitr.R
import com.example.krishimitr.databinding.FragmentSignUpBinding
import com.example.krishimitr.models.Farmer
import com.example.krishimitr.utils.Temp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit


class SignUpFragment : Fragment() {

    private var binding: FragmentSignUpBinding? = null
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    lateinit var phoneNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setListener()
    }

    private fun setListener() {
        binding!!.apply {
            btnSignUp.setOnClickListener {
                val firstName = firstNameEt.text.toString()
                val lastname = lastNameEt.text.toString()
                phoneNumber = numberEt.text.toString()
                val email = emailEt.text.toString().trim()
                val password = passwordEt.text.toString()
                val confirmPassword = confirmPasswordEt.text.toString()
                if (firstName.isNotEmpty() && phoneNumber.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword && phoneNumber.length == 10) {
                    database = FirebaseDatabase.getInstance().getReference("Farmers")
                    val farmer = Farmer(firstName, lastname, "+91$phoneNumber", email, password,auth.uid,false)
                    database.child("$firstName $lastname - $phoneNumber").setValue(farmer)
                        .addOnSuccessListener {

                            phoneNumber = "+91$phoneNumber"
                            val options = PhoneAuthOptions.newBuilder(auth)
                                .setPhoneNumber(phoneNumber) // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(requireActivity()) // Activity (for callback binding)
                                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                                .build()
                            PhoneAuthProvider.verifyPhoneNumber(options)

                            if(email.isNotEmpty()){
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(requireActivity()) { task ->
                                        if (task.isSuccessful) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("TAG", "createUserWithEmail:success")
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("TAG", "createUserWithEmail:failure", task.exception)
                                            Toast.makeText(
                                                requireContext(),
                                                "Authentication failed.",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                        }
                                    }

                            }

                        }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Registeration Failed", Toast.LENGTH_SHORT)
                            .show()
                            Log.d("TAG",it.toString())
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please fill the correct details",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Intent(requireContext(), MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }.also { startActivity(it) }
                    //startActivity(Intent(requireContext(),MainActivity::class.java))
                    Toast.makeText(requireContext(), "Authentication Successfull", Toast.LENGTH_SHORT)

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG",task.exception.toString())
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

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
                Log.d("Tag", "On Verificatoin Failed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("Tag", "On Verificatoin Failed: ${e.toString()}")
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Temp.resendToken=token
            findNavController().navigate(
                R.id.verificationFragment, Bundle().apply {
                    putString("OTP", verificationId)
                    putString("PhoneNumber", phoneNumber)
                }
            )
            // Save verification ID and resending token so we can use them later
        }
    }

}