package com.example.chatapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.modal.User
import com.example.chatapp.onIteamCall

class CallAdapter(private val context: Context, private var userList:ArrayList<User>, private val onIteamCall: onIteamCall):RecyclerView.Adapter<CallAdapter.ViewHolder>() {


    fun filterList(filterList: ArrayList<User>) {
        userList = filterList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val iteamView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_call, parent, false)
        return ViewHolder(iteamView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = userList[position].name.toString()

        holder.ph.text = userList[position].number.toString()
        holder.callUser.setOnClickListener {
            onIteamCall.onCalling(position)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }


    class ViewHolder(iteamView: View) : RecyclerView.ViewHolder(iteamView) {
        var name = iteamView.findViewById<TextView>(R.id.UserName)
        var ph = iteamView.findViewById<TextView>(R.id.UserNumber)
        var callUser: ImageView = iteamView.findViewById(R.id.userCall)


    }


}