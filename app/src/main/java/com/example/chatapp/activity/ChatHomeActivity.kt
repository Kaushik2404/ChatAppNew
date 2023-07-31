package com.example.chatapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.chatapp.R
import com.example.chatapp.adapter.ViewPagerAdapter
import com.example.chatapp.databinding.ActivityChatHomeBinding
import com.example.chatapp.modal.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging


class ChatHomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatHomeBinding
    private var userList = ArrayList<User>()
    var mSlideTest = false
    private val db = FirebaseFirestore.getInstance()
    var id=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userList = arrayListOf()

        Firebase.messaging.subscribeToTopic("weather")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d("TAGMSG", msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }





        drawerSet()
        clickEvent()
        setDataProfile()
        setViewPager()

    }

    private fun addtoken(id: String) {
        Log.d("TAGID",id)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
//                textview.text = "Fetching FCM registration token failed"
                return@OnCompleteListener
            }

            // fetching the token
            val token = task.result

//            textview.text = "Token saved successfully!"

            db.collection("User").document(id).update("token",token.toString())
                .addOnSuccessListener {
                    Log.d("TAGID", "update Token$token")
                }

//            Toast.makeText(
//                baseContext,
//                "Firebase Generated Successfully and saved to realtime database",
//                Toast.LENGTH_SHORT
//            ).show()

        })


    }

    private fun setViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewpager.adapter = adapter

        binding.viewpager.isUserInputEnabled=false

        binding.viewpager.registerOnPageChangeCallback(object : OnPageChangeCallback() {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels:Int,
            ){
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("Selected_Page", position.toString())

                if(position==0){
                    binding.callPage.setBackgroundResource(0)
                    binding.profilePage.setBackgroundResource(0)
                    binding.chatPage.setBackgroundResource(R.drawable.selectediteam)
                    binding.toolbar.title.text="Chat Message"
                }
                else if(position==1){
                    binding.callPage.setBackgroundResource(R.drawable.selectediteam)
                    binding.profilePage.setBackgroundResource(0)
                    binding.chatPage.setBackgroundResource(0)
                    binding.toolbar.title.text="Call"
                }
                else if (position==2){
                    binding.callPage.setBackgroundResource(0)
                    binding.profilePage.setBackgroundResource(R.drawable.selectediteam)
                    binding.chatPage.setBackgroundResource(0)
                    binding.toolbar.title.text="Profile"
                }
            }
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
    }

    private fun drawerSet() {
        binding.myDrawerLayout.addDrawerListener(object :
            ActionBarDrawerToggle(this, binding.myDrawerLayout, 0, 0) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                mSlideTest = true
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                mSlideTest = false
            }
        })
    }

    private fun clickEvent() {
        binding.toolbar.back.setOnClickListener {
            if (mSlideTest) {
                binding.myDrawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.myDrawerLayout.openDrawer(GravityCompat.START)
            }
        }
        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.chatPage.setOnClickListener {
            binding.callPage.setBackgroundResource(0)
            binding.profilePage.setBackgroundResource(0)
            binding.chatPage.setBackgroundResource(R.drawable.selectediteam)
            binding.viewpager.currentItem = 0
            binding.toolbar.title.text="Chat Message"
            binding.toolbar.btnPageFollow.visibility=View.VISIBLE
        }

        binding.callPage.setOnClickListener {
            binding.callPage.setBackgroundResource(R.drawable.selectediteam)
            binding.profilePage.setBackgroundResource(0)
            binding.chatPage.setBackgroundResource(0)
            binding.viewpager.currentItem = 1
            binding.toolbar.title.text="Call"
            binding.toolbar.btnPageFollow.visibility=View.GONE

        }

        binding.profilePage.setOnClickListener {
            binding.callPage.setBackgroundResource(0)
            binding.profilePage.setBackgroundResource(R.drawable.selectediteam)
            binding.chatPage.setBackgroundResource(0)
            binding.viewpager.currentItem = 2
            binding.toolbar.title.text="Profile"
            binding.toolbar.btnPageFollow.visibility=View.GONE
        }

        binding.toolbar.btnPageFollow.setOnClickListener {
            intent= Intent(applicationContext,FollowActivity::class.java)
            startActivity(intent)
        }

    }
    private fun setDataProfile() {
        db.collection("User").addSnapshotListener { value, error ->
            value?.let {
                if (!it.isEmpty) {
                    userList.clear()
                    for (document in it.documents) {
                        if (FirebaseAuth.getInstance().currentUser?.email == document.get("email")) {
                            val user = document.toObject(User::class.java)
                            binding.drawerHeder.profileName.text = user?.name.toString()
                        }
                    }


                }
            }
        }

//        db.collection("User")
//            .get()
//            .addOnSuccessListener {
//                if (!it.isEmpty) {
//                    for (document in it.documents) {
//                        if(FirebaseAuth.getInstance().currentUser?.email==document.get("email")){
//                            val user = document.toObject(User::class.java)
//
//                            break
//                        }
//
//                    }
//
//                }
//
//            }
    }

}