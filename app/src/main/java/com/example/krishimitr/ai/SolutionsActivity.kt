package com.example.krishimitr.ai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.krishimitr.R
import com.example.krishimitr.chat.ExpertListActivity
import com.example.krishimitr.databinding.ActivitySolutionsBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class SolutionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySolutionsBinding
    private lateinit var disease: String
    private lateinit var client:OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySolutionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        disease = intent.getStringExtra("Disease").toString()
        initView()
        getResponse("Give Information about $disease and how can we treat it. Also suggest some product to treat this."){response->
            runOnUiThread {
                binding.aboutDisease.text = response
                binding.aboutDisease.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }

        binding.btnConnectExpert.setOnClickListener {
            val intent = Intent(this, ExpertListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initView() {
        binding.diseaseNameTv.text = disease
        binding.aboutDisease.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        client = OkHttpClient()
    }

    fun getResponse(question:String, callback: (String) -> Unit){

        val apiKey="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"
        val url="https://api.openai.com/v1/engines/text-davinci-003/completions"

        val requestBody="""
            {
            "prompt": "Give Information about $disease and how can we treat it. Also suggest some product to treat this.",
            "max_tokens": 500,
            "temperature": 0
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error","API failed",e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body=response.body?.string()
                if (body != null) {
                    Log.v("data",body)
                }
                else{
                    Log.v("data","empty")
                }
                val jsonObject= JSONObject(body)
                val jsonArray: JSONArray =jsonObject.getJSONArray("choices")
                val textResult=jsonArray.getJSONObject(0).getString("text")
                callback(textResult)

            }
        })
    }

}