package com.example.chatapp.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.chatapp.R
import com.example.chatapp.data.modal.User
import com.example.chatapp.interfacefile.onIteamCall
import de.hdodenhof.circleimageview.CircleImageView

class CallAdapter(private val context: Context, private var userList:ArrayList<User>, private val onIteamCall: onIteamCall):
    RecyclerView.Adapter<CallAdapter.ViewHolder>() {


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

        holder.progress.visibility=View.VISIBLE

        if(userList[position].profileImg!=null){
            val image=userList[position].profileImg?.toUri()

            Glide.with(context).load(userList[position].profileImg)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target:com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        holder.progress.visibility=View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        holder.progress.visibility=View.GONE
                        return false
                    }

                })
                .into(holder.profilePic)
        }else{

            holder.progress.visibility=View.GONE
            holder.profilePic.setImageResource(R.drawable.profile)

        }

    }

    override fun getItemCount(): Int {
        return userList.size
    }


    class ViewHolder(iteamView: View) : RecyclerView.ViewHolder(iteamView) {
        var name = iteamView.findViewById<TextView>(R.id.UserName)
        var ph = iteamView.findViewById<TextView>(R.id.UserNumber)
        var callUser: ImageView = iteamView.findViewById(R.id.userCall)
        var progress: ProgressBar = iteamView.findViewById(R.id.progrsscall)
        var profilePic: CircleImageView = iteamView.findViewById(R.id.profileImagecall)


    }


}