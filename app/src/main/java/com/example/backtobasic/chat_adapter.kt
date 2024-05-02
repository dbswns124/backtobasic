package com.example.backtobasic

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView

class chat_adapter (val users : MutableList<String>) :
    RecyclerView.Adapter<chat_adapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): chat_adapter.ViewHolder {
        // 여기서 가져옴
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chatting_layout , parent, false )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(users[position])
    }


    /*
        interface Itemclick{
            fun onclick(view : View, position: Int)
        }

        var itemclick : Itemclick? = null

    */


/*
        if(itemclick != null) {
            holder.itemView.setOnClickListener { v ->
                itemclick?.onclick(v, position)
            }
        }
        */

        // 여기서 binding : recyclerview의 item에 넣어서 내가 원하는 activity의 xml로 넘겨주는 것


    override fun getItemCount(): Int {
        // 여기서는 몇개인지 알려줌
        return users.size

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindItems(item : String){
            val rv_text = itemView.findViewById<TextView>(R.id.chat_textplace)
            rv_text.text = item
        }
    }

}