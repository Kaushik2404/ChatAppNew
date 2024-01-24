package com.example.chatapp.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.chatapp.interfacefile.OnClickFollow
import com.example.chatapp.R
import com.example.chatapp.data.modal.User
import com.example.chatapp.interfacefile.OnClickGroupAdd
import de.hdodenhof.circleimageview.CircleImageView

class UserGroupAdapter(private val context: Context, private var userList:ArrayList<User>,val onClickGroupAdd: OnClickGroupAdd):RecyclerView.Adapter<UserGroupAdapter.ViewHolder>()  {

    class ViewHolder(view:View):RecyclerView.ViewHolder(view) {
        val name:TextView=view.findViewById(R.id.UserNameGroup)
        val selectBox:CheckBox=view.findViewById(R.id.select_user_group)
        val progressGroup: ProgressBar =view.findViewById(R.id.progress_group)
        val profileImageGroup: CircleImageView =view.findViewById(R.id.profile_image_group)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.group_user,
           parent,false)
        return ViewHolder(itemView)
    }
    override fun getItemCount(): Int {
       return userList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.name.text=userList[position].name.toString()

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
                        holder.progressGroup.visibility=View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        holder.progressGroup.visibility=View.GONE
                        return false
                    }

                })
                .into(holder.profileImageGroup)
        }else{

            holder.progressGroup.visibility=View.GONE
            holder.profileImageGroup.setImageResource(R.drawable.profile)

        }

        holder.selectBox.setOnClickListener {
            if(holder.selectBox.isChecked){
                onClickGroupAdd.onClickUserAdd(position)
            }
            else{
                onClickGroupAdd.onClickUserRemove(position)
            }
        }

    }

}