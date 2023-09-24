package com.example.krishimitr.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.krishimitr.R
import com.example.krishimitr.chat.adapter.MessageAdapter
import com.example.krishimitr.databinding.ActivityChatBinding
import com.example.krishimitr.db.AppPreffManager
import com.example.krishimitr.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    var recieverRoom: String? = null
    var senderRoom: String? = null
    private lateinit var mDbRef: DatabaseReference
    private lateinit var sharePref: AppPreffManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharePref = AppPreffManager(this@ChatActivity)
        val name = intent.getStringExtra("name")
        val recieverUid = intent.getStringExtra("uid")!!.replace(".","")
        val senderUid = sharePref.currUserUid.replace(".","")

        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = recieverUid + senderUid
        recieverRoom = senderUid + recieverUid

        binding.expertName.text = name
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)
        binding.chatRv.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(this@ChatActivity)
        }

        mDbRef.child("chats").child(senderRoom!!)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()
                    for(postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        binding.btnSend.setOnClickListener {
            val msg = binding.messageET.text.toString()
            val message = Message(msg,senderUid)
            mDbRef.child("chats").child(senderRoom!!).push()
                .setValue(message).addOnSuccessListener {
                    mDbRef.child("chats").child(recieverRoom!!).push()
                        .setValue(message)
                }
            binding.messageET.setText("")
        }



        Log.d("mytag",messageList.toString())
    }
}