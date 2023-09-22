package com.example.chatapp.adapter

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
import com.example.chatapp.interfacefile.onClickMsg
import com.google.firebase.auth.FirebaseAuth

class MesssageAdapter(val context: android.content.Context, val msgList: ArrayList<Message>, val onClickMsg: onClickMsg):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
   val ITEM_RECIVE=1
    val ITEM_SENT=2

    val img_sent=3
    val img_get=4

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

//    override fun getItemViewType(position: Int): Int {
//
//        val currentMessage=msgList[position]
//
//
////        if(currentMessage.type=="image"){
////
////            if(FirebaseAuth.getInstance().currentUser?.email.toString()==currentMessage.sendId){
////                return img_sent
////            }
////            else{
////                return img_get
////            }
////        }
////        else{
////            if(FirebaseAuth.getInstance().currentUser?.email.toString()==currentMessage.sendId){
////                return ITEM_SENT
////            }
////            else{
////                return ITEM_RECIVE
////            }
////        }
//
//    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        if(msgList[position].view.toString()=="ON"){
            if(msgList[position].type.toString()=="image"){
                if(FirebaseAuth.getInstance().currentUser?.email.toString()==msgList[position].sendId){
                    if(holder.javaClass==GetViewHolder::class.java){
                        val viewHolder=holder as GetViewHolder
                        viewHolder.sentContactView.visibility=View.GONE
                        viewHolder.getContactView.visibility=View.GONE
                        viewHolder.sendimg.visibility=View.VISIBLE
                        viewHolder.flsendImg.visibility=View.VISIBLE
                        viewHolder.flgetImg.visibility=View.GONE
                        viewHolder.getimg.visibility=View.GONE
                        viewHolder.getMsg.visibility=View.GONE
                        viewHolder.sendMsg.visibility=View.GONE

                        viewHolder.sentPdfView.visibility=View.GONE
                        viewHolder.getPdfView.visibility=View.GONE
//                       Glide.with(context).load(msgList[position].msg).into(viewHolder.sendimg)
                        holder.sendprogress.visibility=View.VISIBLE
                        Glide.with(context).load(msgList[position].msg)
                            .listener(object :RequestListener<Drawable>{
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    holder.sendprogress.visibility=View.GONE
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    holder.sendprogress.visibility=View.GONE
                                    return false
                                }

                            })
                            .into(viewHolder.sendimg)
                    }
                }else{
                    if(holder.javaClass==GetViewHolder::class.java){
                        val viewHolder=holder as GetViewHolder
                        viewHolder.sentContactView.visibility=View.GONE
                        viewHolder.getContactView.visibility=View.GONE
                        viewHolder.flsendImg.visibility=View.GONE
                        viewHolder.flgetImg.visibility=View.VISIBLE
                        viewHolder.getimg.visibility=View.VISIBLE
                        viewHolder.sendimg.visibility=View.GONE
                        viewHolder.sendMsg.visibility=View.GONE
                        viewHolder.getMsg.visibility=View.GONE

                        viewHolder.sentPdfView.visibility=View.GONE
                        viewHolder.getPdfView.visibility=View.GONE
//                        Glide.with(context).load(msgList[position].msg).into(viewHolder.getimg)
                        holder.recprogress.visibility=View.VISIBLE
                        Glide.with(context).load(msgList[position].msg)
                            .listener(object :RequestListener<Drawable>{
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    holder.recprogress.visibility=View.GONE
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    holder.recprogress.visibility=View.GONE
                                    return false
                                }

                            })
                            .into(viewHolder.getimg)

                    }
                }
            }
            else if(msgList[position].type.toString()=="contact"){
                if(FirebaseAuth.getInstance().currentUser?.email.toString()==msgList[position].sendId){
                    if(holder.javaClass==GetViewHolder::class.java){
                        val viewHolder=holder as GetViewHolder

                        viewHolder.sentContactView.visibility=View.VISIBLE
                        viewHolder.getContactView.visibility=View.GONE
                        viewHolder.sendimg.visibility=View.GONE
                        viewHolder.flsendImg.visibility=View.GONE
                        viewHolder.flgetImg.visibility=View.GONE
                        viewHolder.getimg.visibility=View.GONE
                        viewHolder.getMsg.visibility=View.GONE
                        viewHolder.sendMsg.visibility=View.GONE
                        viewHolder.sentPdfView.visibility=View.GONE
                        viewHolder.getPdfView.visibility=View.GONE

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
                        viewHolder.sentContactView.visibility=View.GONE
                        viewHolder.getContactView.visibility=View.VISIBLE
                        viewHolder.flsendImg.visibility=View.GONE
                        viewHolder.flgetImg.visibility=View.GONE
                        viewHolder.getimg.visibility=View.GONE
                        viewHolder.sendimg.visibility=View.GONE
                        viewHolder.sendMsg.visibility=View.GONE
                        viewHolder.getMsg.visibility=View.GONE

                        viewHolder.sentPdfView.visibility=View.GONE
                        viewHolder.getPdfView.visibility=View.GONE

                        val ContactMsg=msgList[position].msg.toString()
                        val list=ContactMsg.split("+")
                        Log.d("TAGcontact",list[1].toString())
                        Log.d("TAGcontact",list[0].toString())
                        viewHolder.mgName.text=list[0].toString()
                        viewHolder.mgNumber.text="+"+list[1]
                    }
                }
            }
            else if(msgList[position].type.toString()=="PDF"){
                if(FirebaseAuth.getInstance().currentUser?.email.toString()==msgList[position].sendId){
                    if(holder.javaClass==GetViewHolder::class.java){
                        val viewHolder=holder as GetViewHolder

                        viewHolder.sentPdfView.visibility=View.VISIBLE
                        viewHolder.getPdfView.visibility=View.GONE
                        viewHolder.sentContactView.visibility=View.GONE
                        viewHolder.getContactView.visibility=View.GONE
                        viewHolder.sendimg.visibility=View.GONE
                        viewHolder.flsendImg.visibility=View.GONE
                        viewHolder.flgetImg.visibility=View.GONE
                        viewHolder.getimg.visibility=View.GONE
                        viewHolder.getMsg.visibility=View.GONE
                        viewHolder.sendMsg.visibility=View.GONE

                        val pdfUri =msgList[position].msg?.toUri()
                        val pdfName=getFileName(pdfUri!!)

                        viewHolder.sendPdfName.text=pdfName.toString()
                        viewHolder.msgLayout.setOnClickListener {
                            onClickMsg.onClickMsg(position)
                        }


                    }
                }
                else{
                    if(holder.javaClass==GetViewHolder::class.java){
                        val viewHolder=holder as GetViewHolder
                        viewHolder.sentPdfView.visibility=View.GONE
                        viewHolder.getPdfView.visibility=View.VISIBLE
                        viewHolder.sentContactView.visibility=View.GONE
                        viewHolder.getContactView.visibility=View.GONE
                        viewHolder.flsendImg.visibility=View.GONE
                        viewHolder.flgetImg.visibility=View.GONE
                        viewHolder.getimg.visibility=View.GONE
                        viewHolder.sendimg.visibility=View.GONE
                        viewHolder.sendMsg.visibility=View.GONE
                        viewHolder.getMsg.visibility=View.GONE

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
                        viewHolder.sentContactView.visibility=View.GONE
                        viewHolder.getContactView.visibility=View.GONE
                        viewHolder.flsendImg.visibility=View.GONE
                        viewHolder.flgetImg.visibility=View.GONE
                        viewHolder.sendMsg.visibility=View.VISIBLE
                        viewHolder.getMsg.visibility=View.GONE
                        viewHolder.sendimg.visibility=View.GONE
                        viewHolder.getimg.visibility=View.GONE

                        viewHolder.sentPdfView.visibility=View.GONE
                        viewHolder.getPdfView.visibility=View.GONE
                        viewHolder.sendMsg.text=msgList[position].msg.toString()
                    }
                }else{
                    if(holder.javaClass==GetViewHolder::class.java){
                        val viewHolder=holder as GetViewHolder
                        viewHolder.sentContactView.visibility=View.GONE
                        viewHolder.getContactView.visibility=View.GONE
                        viewHolder.flsendImg.visibility=View.GONE
                        viewHolder.flgetImg.visibility=View.GONE
                        viewHolder.getMsg.visibility=View.VISIBLE
                        viewHolder.sendMsg.visibility=View.GONE
                        viewHolder.sendimg.visibility=View.GONE
                        viewHolder.getimg.visibility=View.GONE

                        viewHolder.sentPdfView.visibility=View.GONE
                        viewHolder.getPdfView.visibility=View.GONE
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




//        if(holder.javaClass== SentViewHolder::class.java){
//            //do the stuff for sent view holder
//            val viewHolder=holder as SentViewHolder
//            viewHolder.sendMsg.text=msgList[position].msg
//            viewHolder.sendMsg.setOnLongClickListener {
//
//                onClickMsg.onLongClickMsg(position)
//                return@setOnLongClickListener true
//            }
//
//
//        }
//        else if (holder.javaClass== SentImage::class.java){
//            val viewHolder=holder as SentImage
////            holder.sendimg.
//            holder.sendprogress.visibility=View.VISIBLE
//            Glide.with(context).load(msgList[position].msg)
//                .listener(object :RequestListener<Drawable>{
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        isFirstResource: Boolean,
//                    ): Boolean {
//                        holder.sendprogress.visibility=View.GONE
//                        return false
//                    }
//
//                    override fun onResourceReady(
//                        resource: Drawable?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        dataSource: DataSource?,
//                        isFirstResource: Boolean,
//                    ): Boolean {
//                        holder.sendprogress.visibility=View.GONE
//                        return false
//                    }
//
//                })
//                .into(viewHolder.sendimg)
//            viewHolder.sendimg.setOnLongClickListener {
//
//                onClickMsg.onLongClickMsg(position)
//                return@setOnLongClickListener true
//            }
//        }
//       else if (holder.javaClass== getImage::class.java){
//            val viewHolder=holder as getImage
////            holder.sendimg.
//            holder.recprogress.visibility=View.VISIBLE
//            Glide.with(context).load(msgList[position].msg)
//                .listener(object :RequestListener<Drawable>{
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        isFirstResource: Boolean,
//                    ): Boolean {
//                        holder.recprogress.visibility=View.GONE
//                        return false
//                    }
//
//                    override fun onResourceReady(
//                        resource: Drawable?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        dataSource: DataSource?,
//                        isFirstResource: Boolean,
//                    ): Boolean {
//                        holder.recprogress.visibility=View.GONE
//                        return false
//                    }
//
//                })
//                .into(viewHolder.getimg)
//
//            viewHolder.getimg.setOnLongClickListener {
//
//                onClickMsg.onLongClickMsg(position)
//                return@setOnLongClickListener true
//            }
//        }
//       else if(holder.javaClass== GetViewHolder::class.java){
//            //do the stuff for recive view holder
//            val viewHolder=holder as GetViewHolder
//            viewHolder.getMsg.text=msgList[position].msg
//            viewHolder.getMsg.setOnLongClickListener {
//
//                onClickMsg.onLongClickMsg(position)
//                return@setOnLongClickListener true
//            }
//        }
//        else{
//            Log.d("Tag111","no view")
//        }
    }

//    class SentViewHolder(view: android.view.View):RecyclerView.ViewHolder(view){
//
//        val sendMsg=view.findViewById<TextView>(R.id.sendMsg)
//
//    }
    class GetViewHolder(view: android.view.View):RecyclerView.ViewHolder(view){
        val getMsg=view.findViewById<TextView>(R.id.getMsg)
        val sendMsg=view.findViewById<TextView>(R.id.sendMsg)
        val sendimg=view.findViewById<ImageView>(R.id.sendimg)
        val sendprogress=view.findViewById<ProgressBar>(R.id.sendrecprogress)
        val getimg=view.findViewById<ImageView>(R.id.getImg)
        val recprogress=view.findViewById<ProgressBar>(R.id.recprogress)
        val flsendImg=view.findViewById<FrameLayout>(R.id.flsendImg)
        val flgetImg=view.findViewById<FrameLayout>(R.id.flgetImg)
        val msgLayout=view.findViewById<ConstraintLayout>(R.id.msgLayout)
        val sentContactView=view.findViewById<LinearLayout>(R.id.sentContactView)
        val getContactView=view.findViewById<LinearLayout>(R.id.getContactView)
        val msName=view.findViewById<TextView>(R.id.msName)
        val msNumber=view.findViewById<TextView>(R.id.msNumber)
        val mgName=view.findViewById<TextView>(R.id.mgName)
        val mgNumber=view.findViewById<TextView>(R.id.mgNumber)

    val sentPdfView=view.findViewById<LinearLayout>(R.id.sentPdfView)
    val getPdfView=view.findViewById<LinearLayout>(R.id.getPdfView)
    val sendPdfName=view.findViewById<TextView>(R.id.sendpdfName)
    val getPdfName=view.findViewById<TextView>(R.id.getpdfName)

    }

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
//    class SentImage(view: View):RecyclerView.ViewHolder(view){
//        val sendimg=view.findViewById<ImageView>(R.id.sendimg)
//        val sendprogress=view.findViewById<ProgressBar>(R.id.sendrecprogress)
//    }
//    class getImage(view: View):RecyclerView.ViewHolder(view){
//        val getimg=view.findViewById<ImageView>(R.id.getImg)
//        val recprogress=view.findViewById<ProgressBar>(R.id.recprogress)
//    }
}