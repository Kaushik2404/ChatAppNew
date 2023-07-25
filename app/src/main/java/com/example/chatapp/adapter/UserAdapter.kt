package com.example.chatapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.OnIteamClickUser
import com.example.chatapp.R
import com.example.chatapp.modal.User

class UserAdapter(private val context: Context, private var userList:ArrayList<User>, val onIteamClickUser: OnIteamClickUser):RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    fun filterList(filterList: ArrayList<User>) {
        userList = filterList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val iteamView=LayoutInflater.from(parent.context).inflate(R.layout.list_user,parent,false)
        return ViewHolder(iteamView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text=userList[position].name.toString()



        if(userList[position].lastMsg.toString()!=null){
            holder.msg.text=userList[position].lastMsg.toString()
        }
        else{
            holder.msg.text=""
        }

        val lan=userList[position].lastMsgTime.toString().length
        var oktime=""
        for (i in 0..lan){
            if (i<lan-4 && i>lan-10){
                oktime += userList[position].lastMsgTime.toString()[i]
                Log.d("Tag111",oktime)

            }
        }

        if(userList[position].lastMsgTime.toString()!=null){
            holder.time.text=oktime
        }
        else{
            holder.time.text=""
        }

//        holder.time.text=userList[position].lastMsgTime.toString()

        if(userList[position].count==0){
            holder.countbadge.visibility=View.GONE
        }



        else{
            holder.countbadge.visibility=View.VISIBLE
            holder.countbadge.text=userList[position].count.toString()
        }
        holder.layout.setOnClickListener {
            onIteamClickUser.onClickUser(position)
        }
        holder.layout.setOnLongClickListener {

            onIteamClickUser.onLongClick(position)

            return@setOnLongClickListener true
        }

    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(iteamView: View) :RecyclerView.ViewHolder(iteamView){
        var name: TextView =iteamView.findViewById<TextView>(R.id.UserName)
        var msg: TextView =iteamView.findViewById<TextView>(R.id.UserMessage)
        var time: TextView =iteamView.findViewById<TextView>(R.id.UserMessageTime)
        var countbadge: TextView =iteamView.findViewById<TextView>(R.id.countBadge)
//        var msg: TextView =iteamView.findViewById<TextView>(R.id.UserMessage)
        val layout:LinearLayout=iteamView.findViewById(R.id.userView)



    }


}