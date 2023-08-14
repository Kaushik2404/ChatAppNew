package com.example.chatapp.fragment

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.chatapp.R
import com.example.chatapp.activity.IntroActivity
import com.example.chatapp.activity.LoginActivity
import com.example.chatapp.activity.UpdateProfileActivity
import com.example.chatapp.modal.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class UserProfile : Fragment() {

    val db=FirebaseFirestore.getInstance()
    var profileName:TextView?=null
    var profileEmail:TextView?=null
    var profileNumber:TextView?=null
     lateinit   var profileImage:ImageView
//     lateinit   var context: Context

    lateinit var BtnEdit:Button
    lateinit var BtnLogout:Button
    lateinit var Id:String
    lateinit var email:String
    lateinit var name:String
    lateinit var mobile:String
    lateinit var PASS:String
    lateinit var profilePic:CircleImageView
    lateinit var progresss:ProgressBar
    lateinit var BtnDelete:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view=inflater.inflate(R.layout.fragment_user_profile, container, false)
//        context= container!!.context
        profilePic=view.findViewById(R.id.profilePicfrg)
        profileName=view.findViewById(R.id.profileNamefrg)
        profileEmail=view.findViewById(R.id.profileEmailfrg)
        profileNumber=view.findViewById(R.id.profilrNumberfrg)
        profileImage=view.findViewById(R.id.profilePicfrg)
        BtnEdit=view.findViewById(R.id.BtnProfileChange)
        BtnLogout=view.findViewById(R.id.BtnLogOut)
        progresss=view.findViewById(R.id.progrssprofile)
        BtnDelete=view.findViewById(R.id.BtnDelete)

        progresss.visibility=View.VISIBLE


        clickEvent()


        return  view
    }

    private fun clickEvent() {

        BtnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent=Intent(context,LoginActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        }
        BtnEdit.setOnClickListener {
            val intent=Intent(context,UpdateProfileActivity::class.java)
            intent.putExtra("ID",Id)
            intent.putExtra("NAME",name)
            intent.putExtra("EMAIL",email)
            intent.putExtra("NUMBER",mobile)
            intent.putExtra("PASS",PASS)
            activity?.startActivity(intent)

        }

        BtnDelete.setOnClickListener {
            FirebaseFirestore.getInstance().collection("User")
                .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .delete().addOnSuccessListener {
                    val intent=Intent(context,IntroActivity::class.java)
                    activity?.startActivity(intent)
                    activity?.finish()

                }
        }
    }

    override fun onResume() {
        super.onResume()
        setDataProfile()
    }

    override fun onStart() {
        super.onStart()
        setDataProfile()
    }

    private fun setDataProfile() {
        db.collection("User")
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (document in it.documents) {
                        if(FirebaseAuth.getInstance().currentUser?.email==document.get("email")){
                            val user = document.toObject(User::class.java)
                            Id=user?.id.toString()
                            name=user?.name.toString()
                            email=user?.email.toString()
                            mobile=user?.number.toString()
                            PASS=user?.password.toString()
                            profileName?.text=name
                            profileEmail?.text=email
                            profileNumber?.text=mobile

                            if(user?.profileImg!=null){
                                Glide.with(this).load(user?.profileImg)
                                    .listener(object : RequestListener<Drawable> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any?,
                                            target:com.bumptech.glide.request.target.Target<Drawable>?,
                                            isFirstResource: Boolean,
                                        ): Boolean {
                                            progresss.visibility=View.GONE
                                            return false
                                        }

                                        override fun onResourceReady(
                                            resource: Drawable?,
                                            model: Any?,
                                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                                            dataSource: com.bumptech.glide.load.DataSource?,
                                            isFirstResource: Boolean,
                                        ): Boolean {
                                            progresss.visibility=View.GONE
                                            return false
                                        }

                                    })
                                    .into(profileImage)
                            }else{
                                progresss.visibility=View.GONE
                                profileImage.setImageResource(R.drawable.profile)
                            }

                            break
                        }

                    }

                }

            }
    }
}