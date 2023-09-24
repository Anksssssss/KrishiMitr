package com.example.krishimitr.presentation.auth.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.krishimitr.Expert.ExpertActivity
import com.example.krishimitr.MainActivity
import com.example.krishimitr.R
import com.example.krishimitr.databinding.FragmentLoginBinding
import com.example.krishimitr.db.AppPreffManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var sharePref: AppPreffManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharePref = AppPreffManager(requireContext())
        initView()
        setListeners()
    }

    private fun setListeners() {
        binding!!.apply {

                btnLogin.setOnClickListener {
                    val email = binding!!.emailEt.text.toString()
                    val password = binding!!.passwordEt.text.toString()

                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    sharePref.currUserUid = "$email"
                                    if(requireArguments().getString("Expert")=="Expert"){
                                        Intent(requireContext(), ExpertActivity::class.java).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }.also { startActivity(it) }
                                    }else {
                                        Intent(requireContext(), MainActivity::class.java).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }.also { startActivity(it) }
                                    }
                                    //startActivity(Intent(requireContext(), MainActivity::class.java))
                                    //AppPreffManager(requireContext()).setData(true)
                                    Log.d("TAG", "signInWithEmail:success")
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                                    Toast.makeText(
                                        requireContext(),
                                        "Authentication failed.",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                    }
                }
            

           forgotPassword.setOnClickListener {
               findNavController().navigate(
                   R.id.loginViaNumber
               )
           }
            numberLogIn.setOnClickListener {
                findNavController().navigate(
                    R.id.loginViaNumber
                )
            }
          btnSignUp.setOnClickListener {
              findNavController().navigate(
                  R.id.signUpFragment
              )
          }
        }
    }

    private fun initView() {

        auth = FirebaseAuth.getInstance()
        if(requireArguments().getString("Farmer")=="Farmer"){
            binding!!.btnSignUp.visibility = View.VISIBLE
            binding!!.noAccount.visibility = View.VISIBLE
        }else{
            binding!!.btnSignUp.visibility = View.INVISIBLE
            binding!!.noAccount.visibility = View.INVISIBLE
        }
    }


}