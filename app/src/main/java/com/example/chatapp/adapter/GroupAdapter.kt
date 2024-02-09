package com.example.chatapp.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.chatapp.interfacefile.OnIteamClickUser
import com.example.chatapp.R
import com.example.chatapp.data.modal.GroupData
import com.example.chatapp.data.modal.GroupList
import com.example.chatapp.data.modal.User
import com.example.chatapp.interfacefile.onClickMsg
import de.hdodenhof.circleimageview.CircleImageView

class GroupAdapter(private val context: Context, private var groupList:ArrayList<GroupData>,val onClickMsg: onClickMsg):RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val iteamView=LayoutInflater.from(parent.context).inflate(R.layout.group_layout,parent,false)
        return ViewHolder(iteamView)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.name.text=groupList[position].groupName
            holder.lasTMessage.text=groupList[position].lastMsg

        val lan=groupList[position].lastTime.toString().length
        var oktime=""
        for (i in 0..lan){
            if (i<lan-4 && i>lan-10){
                oktime += groupList[position].lastTime.toString()[i]
                Log.d("Tag111",oktime)
            }
        }
        if(groupList[position].lastTime.toString()!=null){
            holder.lastTime.text=oktime
        }
        else{
            holder.lastTime.text=""
        }
//            holder.lastTime.text=groupList[position].lastTime

            holder.layout.setOnClickListener {
                onClickMsg.onClickMsg(position)
            }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    class ViewHolder(iteamView: View) :RecyclerView.ViewHolder(iteamView){
        var name: TextView =iteamView.findViewById<TextView>(R.id.group_name)
        val layout:LinearLayout=iteamView.findViewById(R.id.group_layout)
        val lastTime:TextView=iteamView.findViewById(R.id.GroupMessageTime)
        val lasTMessage:TextView=iteamView.findViewById(R.id.GroupLastMessage)



    }


}