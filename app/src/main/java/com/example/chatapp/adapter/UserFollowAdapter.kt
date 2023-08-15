package com.example.chatapp.adapter

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.chatapp.OnClickFollow
import com.example.chatapp.OnIteamClickUser
import com.example.chatapp.R
import com.example.chatapp.modal.User
import com.google.firebase.database.collection.LLRBNode.Color
import de.hdodenhof.circleimageview.CircleImageView

class UserFollowAdapter(private val context: Context, private var userList:ArrayList<User>,var onClickFollow: OnClickFollow):RecyclerView.Adapter<UserFollowAdapter.Viewholder>()  {

    class Viewholder(view:View):RecyclerView.ViewHolder(view) {
        val name=view.findViewById<TextView>(R.id.UserNameFollow)
        val followBtn=view.findViewById<TextView>(R.id.btnFollow)
        val progrssfollow=view.findViewById<ProgressBar>(R.id.progrssfollow)
        val profileImagefollow=view.findViewById<CircleImageView>(R.id.profileImagefollow)


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
//            holder.followBtn.setTextColor(R.color.white)
            holder.followBtn.setTextColor(context.resources.getColor(R.color.white))
            onClickFollow.onClickUserFollow(position)
        }

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
                        holder.progrssfollow.visibility=View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        holder.progrssfollow.visibility=View.GONE
                        return false
                    }

                })
                .into(holder.profileImagefollow)
        }else{

            holder.progrssfollow.visibility=View.GONE
            holder.profileImagefollow.setImageResource(R.drawable.profile)

        }


    }

}