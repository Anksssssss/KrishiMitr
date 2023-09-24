package com.example.krishimitr.presentation.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.krishimitr.MainActivity
import com.example.krishimitr.R
import com.example.krishimitr.databinding.FragmentVerificationBinding
import com.example.krishimitr.db.AppPreffManager
import com.example.krishimitr.utils.Temp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class VerificationFragment : Fragment() {

    private var binding: FragmentVerificationBinding? = null
    val phoneNumber = arguments?.getString("PhoneNumber")
    private lateinit var auth: FirebaseAuth
    lateinit var OTP:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVerificationBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        OTP = requireArguments().getString("OTP").toString()
        setListeners()

    }

    private fun setListeners() {
        binding!!.apply {
            mobileNoTv.text = phoneNumber
            isButtonActive(false)

            pinView.addTextChangedListener {
                if (it?.length!! != 6) {
                    isButtonActive(false)
                    return@addTextChangedListener
                }
                isButtonActive(true)
                //hideKeyboard()
            }

            btnVerify.setOnClickListener {
               val typedOTP = pinView.text.toString().trim()
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    OTP,typedOTP
                )
                signInWithPhoneAuthCredential(credential)
            }

            resendBtn.setOnClickListener {
                //phoneNumber = "+91$phoneNumber"
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber!!) // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(requireActivity()) // Activity (for callback binding)
                    .setCallbacks(callbacks)
                    .setForceResendingToken(Temp.resendToken!!)// OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
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
                    //startActivity(Intent(requireContext(), MainActivity::class.java))
                    //AppPreffManager(requireContext()).setData(true)
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


    fun isButtonActive(isActive: Boolean) {
        binding!!.btnVerify.isEnabled = isActive
        binding!!.btnVerify.isClickable = isActive
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
            OTP = verificationId
            Temp.resendToken = token
            // Save verification ID and resending token so we can use them later
        }
    }

}