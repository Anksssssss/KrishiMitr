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
import com.example.krishimitr.databinding.FragmentLoginViaNumberBinding
import com.example.krishimitr.models.Farmer
import com.example.krishimitr.utils.Temp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit

class LoginViaNumber : Fragment() {

    private var binding: FragmentLoginViaNumberBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var phoneNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginViaNumberBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding!!.btnSendOTP.setOnClickListener {
            phoneNumber = binding!!.numberEt.text.toString()
            if (phoneNumber.isNotEmpty() && phoneNumber.length == 10) {
                phoneNumber = "+91$phoneNumber"
                val database = FirebaseDatabase.getInstance()
                val reference: DatabaseReference =
                    database.getReference("Farmers") // Replace with your actual path
                // Create a query
                val query: Query = reference.orderByChild("phoneNumber").equalTo(phoneNumber)

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var found = false // Initialize a flag to track whether data is found

                        // Loop through the results
                        for (snapshot in dataSnapshot.children) {
                            // Access the data you found
                            val farmer = snapshot.getValue(Farmer::class.java)

                            // Use the 'farmer' object as needed
                            //println("Found Farmer: ${farmer?.firstName} ${farmer?.lastName}")

                            found = true // Set the flag to true since data is found
                        }

                        // Check if data is not found
                        if (!found) {
                            Toast.makeText(
                                requireContext(),
                                "Please Signup first!",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Handle the case where the phone number is not found
                        } else {
                            val options = PhoneAuthOptions.newBuilder(auth)
                                .setPhoneNumber(phoneNumber) // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(requireActivity()) // Activity (for callback binding)
                                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                                .build()
                            PhoneAuthProvider.verifyPhoneNumber(options)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle any errors
                        println("Error: ${databaseError.message}")
                    }
                })
            } else {
                Toast.makeText(requireContext(), "Enter Correct Phone Number", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    startActivity(Intent(requireContext(), VerificationFragment::class.java))
                    Toast.makeText(
                        requireContext(),
                        "Authentication Successfull",
                        Toast.LENGTH_SHORT
                    )

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG", task.exception.toString())
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
            Temp.resendToken = token
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