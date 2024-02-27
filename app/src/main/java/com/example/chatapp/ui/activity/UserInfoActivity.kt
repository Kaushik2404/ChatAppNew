package com.example.chatapp.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityUserInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserInfoActivity : AppCompatActivity() {
   lateinit var binding: ActivityUserInfoBinding
   private var profileImage=""
   private var profileName=""
   private var profileId=""
   private var status:Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()
        setData()
        onClick()


    }

    private fun onClick() {
        binding.headerInfoUser.back.setOnClickListener {
            if(status){
                onBackPressed()
            }
            else{
                val intent = Intent(this,ChatHomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        binding.msgUserInfoBtn.setOnClickListener {
            if(status){
                onBackPressed()
            }
            else{
                Toast.makeText(this,"First Follow This User",Toast.LENGTH_SHORT).show()
            }
        }
        binding.followUserInfoBtn.setOnClickListener {
            if(status){
                val dialog = Dialog(this)
                dialog.requestWindowFeature(1)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
                dialog.setContentView(R.layout.dialog_unfollow)
                val textView = dialog.findViewById<TextView>(R.id.tv_yes)
                (dialog.findViewById<View>(R.id.tv_no) as TextView).setOnClickListener { dialog.dismiss() }
                textView.setOnClickListener {
                    FirebaseFirestore.getInstance().collection("User")
                        .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                        .collection("FollowList")
                        .document(profileId)
                        .delete()
                        .addOnSuccessListener {
                            status=false
                            binding.followUserInfoBtn.text= getString(R.string.unfollow)
                            binding.followUserInfoBtn.setTextColor(Color.BLACK)
                            Log.d("TAG11", "DocumentSnapshot successfully deleted!")
                        }
                        .addOnFailureListener { e -> Log.w("TAG11", "Error deleting document", e) }
                    dialog.dismiss()
                }
                dialog.show()
            }
            else{

            }
        }
    }

    private fun setData() {
        setProfileImage()
        binding.userNameInfo.text=profileName
    }

    private fun setProfileImage() {
        if(profileImage !=null){
            Glide.with(applicationContext).load(profileImage)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        binding.progressInfoUserProfile.visibility= View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        binding.progressInfoUserProfile.visibility= View.GONE
                        return false
                    }

                })
                .into(binding.profileImage)
        }
    }

    private fun getData() {
        profileId = intent.getStringExtra("UserID").toString()
        profileImage = intent.getStringExtra("UserProfile").toString()
        profileName= intent.getStringExtra("UserName").toString()

        Log.d("TAG11", "-------UserId$profileId")
        Log.d("TAG11", "-------UserId$profileName")
    }
}