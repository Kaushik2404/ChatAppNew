package com.example.chatapp.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.chatapp.R
import com.example.chatapp.activity.LoginActivity
import com.example.chatapp.activity.UpdateProfileActivity
import com.example.chatapp.modal.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view=inflater.inflate(R.layout.fragment_user_profile, container, false)
//        context= container!!.context
        profileName=view.findViewById(R.id.profileNamefrg)
        profileEmail=view.findViewById(R.id.profileEmailfrg)
        profileNumber=view.findViewById(R.id.profilrNumberfrg)
        profileImage=view.findViewById(R.id.profilePicfrg)
        BtnEdit=view.findViewById(R.id.BtnProfileChange)
        BtnLogout=view.findViewById(R.id.BtnLogOut)



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
                            break
                        }

                    }

                }

            }
    }
}