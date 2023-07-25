package com.example.chatapp.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.OnClickFollow
import com.example.chatapp.OnIteamClickUser
import com.example.chatapp.R
import com.example.chatapp.modal.User
import com.google.firebase.database.collection.LLRBNode.Color

class UserFollowAdapter(private val context: Context, private var userList:ArrayList<User>,var onClickFollow: OnClickFollow):RecyclerView.Adapter<UserFollowAdapter.Viewholder>()  {

    class Viewholder(view:View):RecyclerView.ViewHolder(view) {
        val name=view.findViewById<TextView>(R.id.UserNameFollow)
        val followBtn=view.findViewById<TextView>(R.id.btnFollow)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val iteamView=LayoutInflater.from(parent.context).inflate(R.layout.follow_user,
           parent,false)
        return Viewholder(iteamView)
    }

    override fun getItemCount(): Int {
       return userList.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {

        holder.name.text=userList[position].name.toString()


        holder.followBtn.setOnClickListener {
            holder.followBtn.setBackgroundResource(R.drawable.btn_follow)
            holder.followBtn.setTextColor(R.color.white)
            onClickFollow.onClickUserFollow(position)
        }

    }

}