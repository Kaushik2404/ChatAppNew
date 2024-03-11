package com.example.chatapp.ui.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.chatapp.R
import com.example.chatapp.ui.activity.IntroActivity
import com.example.chatapp.ui.activity.LoginActivity
import com.example.chatapp.ui.activity.UpdateProfileActivity
import com.example.chatapp.data.modal.User
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
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
            val intent=Intent(context, LoginActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        }
        BtnEdit.setOnClickListener {
            val intent=Intent(context, UpdateProfileActivity::class.java)
            intent.putExtra("ID",Id)
            intent.putExtra("NAME",name)
            intent.putExtra("EMAIL",email)
            intent.putExtra("NUMBER",mobile)
            intent.putExtra("PASS",PASS)
            activity?.startActivity(intent)

        }

        BtnDelete.setOnClickListener {

            showDeleteDilog()
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
    private fun showDeleteDilog(){
        val email:EditText
        val pass:EditText
        val delete:Button

        val alert=AlertDialog.Builder(context)
        val view=layoutInflater.inflate(R.layout.delete_dialog,null)

        email=view.findViewById(R.id.deleteEmail)
        pass=view.findViewById(R.id.deletePassword)
        delete=view.findViewById(R.id.btndelete)

        alert.setView(view)
        alert.setCancelable(true)
        val dialog =alert.create()
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.show()

        delete.setOnClickListener {
            val mProgressDialog = ProgressDialog(context)
            mProgressDialog.setMessage("Please Wait....")
            mProgressDialog.show()
            dialog.dismiss()

            if(email.text.toString().isNotEmpty() && pass.text.toString().isNotEmpty()){
                FirebaseFirestore.getInstance().collection("User")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .delete().addOnSuccessListener {
                        val credential=EmailAuthProvider.getCredential(email.text.toString(),pass.text.toString())
                        FirebaseAuth.getInstance().currentUser?.reauthenticate(credential)
                            ?.addOnCompleteListener {
                                FirebaseAuth.getInstance().currentUser?.delete()
                                    ?.addOnSuccessListener {
                                        mProgressDialog.dismiss()
                                            FirebaseAuth.getInstance().signOut()
                                            Toast.makeText(context, "Successfully Deleted User", Toast.LENGTH_SHORT).show()
                                            val intent=Intent(context, IntroActivity::class.java)
                                            activity?.startActivity(intent)
                                            activity?.finish()
                                    }
                                    ?.addOnFailureListener {
                                        Toast.makeText(context, "email or passsword not Correct", Toast.LENGTH_SHORT).show()
                                    }
                            }
                    }
            }else{
                mProgressDialog.dismiss()
                Toast.makeText(context, "Filed not blank", Toast.LENGTH_SHORT).show()
            }
        }
    }
}