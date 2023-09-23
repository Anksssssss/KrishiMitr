package com.example.krishimitr.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.krishimitr.R
import com.example.krishimitr.chat.adapter.ExpertAdapter
import com.example.krishimitr.models.Farmer

class ExpertListActivity : AppCompatActivity() {

    private lateinit var expertAdapter: ExpertAdapter
    private lateinit var expertList: ArrayList<Farmer>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expert_list)
    }
}