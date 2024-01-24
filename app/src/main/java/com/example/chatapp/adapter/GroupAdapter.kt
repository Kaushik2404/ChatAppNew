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




    }


}