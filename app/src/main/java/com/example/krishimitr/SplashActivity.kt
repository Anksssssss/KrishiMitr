package com.example.krishimitr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import com.example.krishimitr.databinding.ActivitySplashBinding
import com.example.krishimitr.db.AppPreffManager
import com.example.krishimitr.presentation.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        //setTheme(R.style.Theme_KrishiMitr)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        Handler(Looper.getMainLooper()).postDelayed(2000){
            if(auth.currentUser != null){
                startActivity(Intent(this@SplashActivity,MainActivity::class.java))
            }else{
                startActivity(Intent(this@SplashActivity,LoginActivity::class.java))
            }
            finish()
        }
    }
}