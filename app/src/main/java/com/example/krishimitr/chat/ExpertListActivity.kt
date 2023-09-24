package com.example.krishimitr.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.krishimitr.R
import com.example.krishimitr.chat.adapter.ExpertAdapter
import com.example.krishimitr.databinding.ActivityExpertBinding
import com.example.krishimitr.databinding.ActivityExpertListBinding
import com.example.krishimitr.db.AppPreffManager
import com.example.krishimitr.models.Farmer
import com.example.krishimitr.utils.Temp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ExpertListActivity : AppCompatActivity() {

    private lateinit var expertAdapter: ExpertAdapter
    private lateinit var expertList: ArrayList<Farmer>
    private lateinit var binding: ActivityExpertListBinding
    private lateinit var mDbRef: DatabaseReference
    private lateinit var sharedPref: AppPreffManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpertListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expertList = ArrayList()
        sharedPref = AppPreffManager(this@ExpertListActivity)
        expertAdapter = ExpertAdapter(
            this,
            expertList,
            onClick = {
                val intent = Intent(this@ExpertListActivity, ChatActivity::class.java)
                intent.putExtra("name",it.firstName)
                intent.putExtra("uid",it.uid)
                startActivity(intent)
            }
            )
        mDbRef = FirebaseDatabase.getInstance().getReference()

        binding.expertListRv.apply {
            adapter = expertAdapter
            layoutManager = LinearLayoutManager(this@ExpertListActivity)
        }

        mDbRef.child("Farmers").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                expertList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(Farmer::class.java)
                    if(currentUser?.uid != sharedPref.currUserUid ) {
                        expertList.add(currentUser!!)
                    }
                }
                expertAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        } )
    }
}