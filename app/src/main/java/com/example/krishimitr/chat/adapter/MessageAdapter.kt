package com.example.krishimitr.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.krishimitr.databinding.ItemRecievedBinding
import com.example.krishimitr.databinding.ItemSentBinding
import com.example.krishimitr.databinding.ItemUserLayoutBinding
import com.example.krishimitr.db.AppPreffManager
import com.example.krishimitr.models.Farmer
import com.example.krishimitr.models.Message
import com.example.krishimitr.utils.Temp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MessageAdapter(
    val context: Context,
    var messageList: ArrayList<Message>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECIEVED = 1
    val ITEM_SENT = 2
    var sharedPref = AppPreffManager(context)

    class SentViewHolder: RecyclerView.ViewHolder{
        private lateinit var binding: ItemSentBinding
        constructor(binding: ItemSentBinding) : super(binding.root) {
            this.binding = binding
        }
        fun bind(message : Message, position: Int){
            binding.apply {
                sentTV.text = message.message.toString()
            }
        }
    }

    class RecieveViewHolder: RecyclerView.ViewHolder{
        private lateinit var binding: ItemRecievedBinding
        constructor(binding: ItemRecievedBinding) : super(binding.root) {
            this.binding = binding
        }
        fun bind(message : Message, position: Int){
            binding.apply {
                recievedTv.text = message.message.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){
            val layoutInflater = LayoutInflater.from(context)
            return MessageAdapter.RecieveViewHolder(
                ItemRecievedBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
        }else{
            val layoutInflater = LayoutInflater.from(context)
            return MessageAdapter.SentViewHolder(
                ItemSentBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if(sharedPref.currUserUid.replace(".","") == currentMessage.senderId){
            return ITEM_SENT
        }else{
            return ITEM_RECIEVED
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.javaClass == SentViewHolder::class.java){
            val viewHolder = holder as SentViewHolder
            viewHolder.bind(messageList[position], position)
        }else{
            val viewHolder = holder as RecieveViewHolder
            viewHolder.bind(messageList[position], position)
        }
    }


}