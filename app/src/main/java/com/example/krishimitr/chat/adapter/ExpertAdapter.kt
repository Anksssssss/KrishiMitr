package com.example.krishimitr.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.krishimitr.databinding.ItemUserLayoutBinding
import com.example.krishimitr.models.Farmer

class ExpertAdapter(
    val context: Context,
    val userList: ArrayList<Farmer>,
    val onClick: (farmer: Farmer) -> Unit
        ): RecyclerView.Adapter<ExpertAdapter.userViewHolder>() {

    inner class userViewHolder:RecyclerView.ViewHolder{
        private lateinit var binding: ItemUserLayoutBinding
        constructor(binding: ItemUserLayoutBinding) : super(binding.root) {
            this.binding = binding
        }
        fun bind(user : Farmer,position: Int){
            binding.apply {
                userName.text = user.firstName
                userSpecialization.text = user.specialization
                expertCard.setOnClickListener {
                    onClick(user)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        return userViewHolder(
            ItemUserLayoutBinding.inflate(
                layoutInflater,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: userViewHolder, position: Int) {
        holder.bind(userList[position],position)
    }
}