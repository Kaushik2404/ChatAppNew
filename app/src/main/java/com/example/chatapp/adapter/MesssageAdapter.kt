package com.example.chatapp.adapter

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.chatapp.R
import com.example.chatapp.data.modal.Message
import com.example.chatapp.data.modal.User
import com.example.chatapp.interfacefile.onClickMsg
import com.example.chatapp.util.GetUserName
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: android.content.Context, private val msgList: ArrayList<Message>, private val userList: ArrayList<User>, private val onClickMsg: onClickMsg):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view= LayoutInflater.from(context).inflate(R.layout.test,parent,false)
        return GetViewHolder(view)
//      if(viewType==1){
//          val view= LayoutInflater.from(context).inflate(R.layout.message_get,parent,false)
//          return GetViewHolder(view)
//      }
//      else if (viewType==3){
//          val view= LayoutInflater.from(context).inflate(R.layout.img_send,parent,false)
//          return SentImage(view)
//      }
//       else if (viewType==4){
//          val view= LayoutInflater.from(context).inflate(R.layout.img_recive,parent,false)
//          return getImage(view)
//      }
//      else{
//          val view= LayoutInflater.from(context).inflate(R.layout.message_send,parent,false)
//          return SentViewHolder(view)
//      }
    }

    override fun getItemCount(): Int {
        return msgList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(msgList[position].view.toString()=="ON"){
            if(msgList[position].type.toString()=="image"){
                if(FirebaseAuth.getInstance().currentUser?.email.toString()==msgList[position].sendId){
                    if(holder.javaClass==GetViewHolder::class.java){
                        val viewHolder=holder as GetViewHolder
                        visibilityOff(viewHolder)
                        viewHolder.sendImg.visibility=View.VISIBLE
                        viewHolder.flSendImg.visibility=View.VISIBLE
                        viewHolder.sendProgress.visibility=View.VISIBLE
                        Glide.with(context).load(msgList[position].msg)
                            .listener(object :RequestListener<Drawable>{
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    holder.sendProgress.visibility=View.GONE
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    holder.sendProgress.visibility=View.GONE
                                    return false
                                }

                            })
                            .into(viewHolder.sendImg)

                        viewHolder.msgLayout.setOnClickListener {
                            onClickMsg.onClickMsg(position)
                        }
                    }
                }else{
                    if(holder.javaClass==GetViewHolder::class.java){
                        val viewHolder=holder as GetViewHolder
                        visibilityOff(viewHolder)
                        if(msgList[position].reciverID?.contains("Group") == true){
                            viewHolder.userName.visibility=View.VISIBLE
                        }
                        var nameOk=""
                        userList.let {
                            it.forEach{ user->
                                if(user.email== msgList[position].sendId){
                                    nameOk=user.name.toString()
                                }
                            }
                        }
                        viewHolder.userName.text=nameOk
                        viewHolder.flGetImg.visibility=View.VISIBLE
                        viewHolder.getImg.visibility=View.VISIBLE
                        holder.recProgress.visibility=View.VISIBLE
                        Glide.with(context).load(msgList[position].msg)
                            .listener(object :RequestListener<Drawable>{
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    holder.recProgress.visibility=View.GONE
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    holder.recProgress.visibility=View.GONE
                                    return false
                                }

                            })
                            .into(viewHolder.getImg)

                        viewHolder.msgLayout.setOnClickListener {
                            onClickMsg.onClickMsg(position)
                        }

                    }
                }
            }
            else if(msgList[position].type.toString()=="contact"){
                if(FirebaseAuth.getInstance().currentUser?.email.toString()==msgList[position].sendId){
                    if(holder.javaClass==GetViewHolder::class.java){
                        val viewHolder=holder as GetViewHolder
                        visibilityOff(viewHolder)
                        viewHolder.sentContactView.visibility=View.VISIBLE
                        val ContactMsg=msgList[position].msg.toString()
                        val list=ContactMsg.split("/")
                        Log.d("TAGcontact",list[1].toString())
                        Log.d("TAGcontact",list[0].toString())
                        if(list[0].isNotEmpty()){
                            viewHolder.msName.text=list[0].toString()
                        }else{
                            viewHolder.msName.text="Unknown.."
                        }
                        if(list[1].isNotEmpty()){
                            viewHolder.msNumber.text=list[1].toString()
                        }else{
                            viewHolder.msNumber.text=""
                        }
//                        viewHolder.msNumber.text="+"+list[1]

                    }
                }
                else{
                    if(holder.javaClass==GetViewHolder::class.java){
                        val viewHolder=holder as GetViewHolder
                        visibilityOff(viewHolder)
                        if(msgList[position].reciverID?.contains("Group") == true){
                            viewHolder.userName.visibility=View.VISIBLE
                        }
                        var nameOk=""
                        userList.let {
                            it.forEach{ user->
                                if(user.email== msgList[position].sendId){
                                    nameOk=user.name.toString()
                                }
                            }
                        }
                        viewHolder.userName.text=nameOk
                        viewHolder.getContactView.visibility=View.VISIBLE
                        val ContactMsg=msgList[position].msg.toString()
                        val list=ContactMsg.split("+")
                        Log.d("TAGcontact",list[1].toString())
                        Log.d("TAGcontact",list[0].toString())
                        viewHolder.mgName.text=list[0].toString()
                        viewHolder.mgNumber.text= "+ ${list[1]}"
                    }
                }
            }
            else if(msgList[position].type.toString()=="PDF"){
                if(FirebaseAuth.getInstance().currentUser?.email.toString()==msgList[position].sendId){
                    if(holder.javaClass==GetViewHolder::class.java) {
                        val viewHolder = holder as GetViewHolder
                        visibilityOff(viewHolder)
                        viewHolder.sentPdfView.visibility = View.VISIBLE
                        val pdfUri = msgList[position].msg?.toUri()
                        val pdfName = getFileName(pdfUri!!)

                        viewHolder.sendPdfName.text = pdfName.toString()
                        viewHolder.msgLayout.setOnClickListener {
                            onClickMsg.onClickMsg(position)
                        }
                    }
                }
                else{
                    if(holder.javaClass==GetViewHolder::class.java){
                        val viewHolder=holder as GetViewHolder
                        visibilityOff(viewHolder)
                        if(msgList[position].reciverID?.contains("Group") == true){
                            viewHolder.userName.visibility=View.VISIBLE
                        }
                        var nameOk=""
                        userList.let {
                            it.forEach{ user->
                                if(user.email== msgList[position].sendId){
                                    nameOk=user.name.toString()
                                }
                            }
                        }
                        viewHolder.userName.text=nameOk
                        viewHolder.getPdfView.visibility=View.VISIBLE
                        val pdfUri =msgList[position].msg?.toUri()
                        val pdfName=getFileName(pdfUri!!)
                        viewHolder.getPdfName.text=pdfName.toString()

                        viewHolder.msgLayout.setOnClickListener {
                            onClickMsg.onClickMsg(position)
                        }
                    }
                }
            }
            else {
                if(FirebaseAuth.getInstance().currentUser?.email.toString()==msgList[position].sendId){

                    if(holder.javaClass==GetViewHolder::class.java){
                        val viewHolder=holder as GetViewHolder
                        visibilityOff(viewHolder)
                        viewHolder.sendMsg.visibility=View.VISIBLE
                        viewHolder.sendMsg.text=msgList[position].msg.toString()
                    }
                }else{
                    if(holder.javaClass==GetViewHolder::class.java){
                        val viewHolder=holder as GetViewHolder
                        visibilityOff(viewHolder)
                        if(msgList[position].reciverID?.contains("Group",false) == true){
                            viewHolder.userName.visibility=View.VISIBLE
                        }
                     var nameOk=""
                        userList.let {
                            it.forEach{ user->
                                if(user.email== msgList[position].sendId){
                                    nameOk=user.name.toString()
                                }
                            }
                        }
                        viewHolder.userName.text=nameOk
                        viewHolder.getMsg.visibility=View.VISIBLE
                        viewHolder.getMsg.text=msgList[position].msg.toString()
                    }
                }
            }
            val viewHolder=holder as GetViewHolder
            viewHolder.msgLayout.setOnLongClickListener {
                onClickMsg.onLongClickMsg(position)
                return@setOnLongClickListener true
            }
        }
    }



    class GetViewHolder(view: android.view.View):RecyclerView.ViewHolder(view){
        val getMsg: TextView =view.findViewById(R.id.getMsg)
        val sendMsg: TextView =view.findViewById(R.id.sendMsg)
        val sendImg: ImageView =view.findViewById(R.id.sendimg)
        val sendProgress: ProgressBar =view.findViewById(R.id.sendrecprogress)
        val getImg: ImageView =view.findViewById(R.id.getImg)
        val recProgress: ProgressBar =view.findViewById(R.id.recprogress)
        val flSendImg: FrameLayout =view.findViewById(R.id.flsendImg)
        val flGetImg: FrameLayout =view.findViewById(R.id.flgetImg)
        val msgLayout: ConstraintLayout =view.findViewById(R.id.msgLayout)
        val sentContactView: LinearLayout =view.findViewById(R.id.sentContactView)
        val getContactView: LinearLayout =view.findViewById(R.id.getContactView)
        val msName: TextView =view.findViewById(R.id.msName)
        val msNumber: TextView =view.findViewById(R.id.msNumber)
        val mgName: TextView =view.findViewById(R.id.mgName)
        val mgNumber: TextView =view.findViewById(R.id.mgNumber)
        val sentPdfView: LinearLayout =view.findViewById(R.id.sentPdfView)
        val getPdfView: LinearLayout =view.findViewById(R.id.getPdfView)
        val sendPdfName: TextView =view.findViewById(R.id.sendpdfName)
        val getPdfName: TextView =view.findViewById(R.id.getpdfName)
        val userName: TextView =view.findViewById(R.id.getMsgUserName)

    }

    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun visibilityOff(viewHolder: GetViewHolder) {
        viewHolder.sentContactView.visibility=View.GONE
        viewHolder.getContactView.visibility=View.GONE
        viewHolder.sendImg.visibility=View.GONE
        viewHolder.flSendImg.visibility=View.GONE
        viewHolder.flGetImg.visibility=View.GONE
        viewHolder.getImg.visibility=View.GONE
        viewHolder.getMsg.visibility=View.GONE
        viewHolder.sendMsg.visibility=View.GONE
        viewHolder.sentPdfView.visibility=View.GONE
        viewHolder.getPdfView.visibility=View.GONE
        viewHolder.sendProgress.visibility=View.GONE
        viewHolder.userName.visibility=View.GONE
    }
}