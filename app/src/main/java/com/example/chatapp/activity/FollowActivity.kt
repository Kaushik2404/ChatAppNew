package com.example.chatapp.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.OnClickFollow
import com.example.chatapp.adapter.UserFollowAdapter
import com.example.chatapp.databinding.ActivityFollowBinding
import com.example.chatapp.modal.User
import com.example.chatapp.modell.Data
import com.example.chatapp.modell.NotificationModel
import com.example.chatapp.ui.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowActivity : AppCompatActivity() {
    lateinit var binding: ActivityFollowBinding
    lateinit var userList: ArrayList<User>
    lateinit var userList2: ArrayList<User>
    lateinit var Token: String
    lateinit var name: String
    lateinit var mainUser: String

    private val TAG = "Chat"
    private val notificationViewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userList = arrayListOf()
        userList2 = arrayListOf()
        currentUserName()
        followList()
        onClick()
    }
    private fun onClick() {
        binding.headerFollow.back.setOnClickListener {
            onBackPressed()
        }
    }
    private fun listenNewMessage() {
        FirebaseFirestore.getInstance().collection("User")
            .addSnapshotListener { value, error ->
                value?.let {
                    if (!it.isEmpty) {
                        userList.clear()
                        for (document in it.documents) {
                            if (document.get("email") == FirebaseAuth.getInstance().currentUser?.email.toString()) {
                                val userId = document.get("id").toString()
                            }
                            if (FirebaseAuth.getInstance().currentUser?.email != document.get(
                                    "email"
                                )
                            ) {
                                val user = document.toObject(User::class.java)
                                userList.add(user!!)
                                Log.d(
                                    "TAG111",
                                    "${document.id} => ${document.data}"
                                )
                            }
                        }
                        userList.sortByDescending { it.lastMsgTime }

                        binding.recViewFollow.layoutManager = LinearLayoutManager(applicationContext)
                        binding.recViewFollow.setHasFixedSize(true)

                        val adapter = UserFollowAdapter(this, userList, object : OnClickFollow {
                            override fun onClickUserFollow(pos: Int) {
                                name = userList[pos].name.toString()
                                getToken(pos)

                                val okuser = User(
                                    userList[pos].profileImg,
                                    userList[pos].id,
                                    userList[pos].name,
                                    userList[pos].email,
                                    userList[pos].number,
                                    userList[pos].password,
                                    userList[pos].lastMsg,
                                    userList[pos].lastMsgTime,
                                    userList[pos].count,
                                    userList[pos].token
                                )

//                                FirebaseFirestore.getInstance()
//                                    .collection(FirebaseAuth.getInstance().currentUser?.email.toString())
//                                    .document(userList[pos].email.toString())
//                                    .set(okuser)
//                                    .addOnSuccessListener {
//                                        Log.d("TAG11", "User Follow")
//                                        notificationCheckCondition()
//                                        push("Follow request")
//                                    }
//                                    .addOnFailureListener {
//                                        Log.d("TAG11", "User not Follow")
//                                    }
                                FirebaseFirestore.getInstance()
                                    .collection("User")
                                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                    .collection("FollowList")
                                    .document(userList[pos].id.toString())
                                    .set(okuser)
                                    .addOnSuccessListener {
                                        Log.d("TAG11", "User Follow")
                                        notificationCheckCondition()
                                        push("Follow request")
                                    }
                                    .addOnFailureListener {
                                        Log.d("TAG11", "User not Follow")
                                    }

                            }

                        }
                        )
                        binding.recViewFollow.adapter = adapter
                    }
                }
            }
    }
//    private fun followList() {
//        FirebaseFirestore.getInstance()
//            .collection(FirebaseAuth.getInstance().currentUser?.email.toString())
//            .addSnapshotListener { value, error ->
//                value?.let {
//                    if (!it.isEmpty) {
//                        Log.d("listUser",userList2.size.toString())
//                        userList2.clear()
//                        for (document in it.documents) {
//                            if (document.get("email") == FirebaseAuth.getInstance().currentUser?.email.toString()) {
//                                val userId = document.get("id").toString()
//                            }
//                            if (FirebaseAuth.getInstance().currentUser?.email != document.get("email")) {
//                                val user = document.toObject(User::class.java)
//                                userList2.add(user!!)
//                                Log.d("TAG111", "${document.id} => ${document.data}")
//                            }
//                        }
//                        Log.d("TAG111333", "${userList2.map { it.email }}")
//                        FirebaseFirestore.getInstance().collection("User")
//                            .whereNotIn("email", userList2.map { it.email})
//                            .addSnapshotListener { value, error ->
//                                value?.let {
//                                    if (!it.isEmpty) {
//                                        userList.clear()
//                                        for (document in it.documents) {
//                                            if (document.get("email") == FirebaseAuth.getInstance().currentUser?.email.toString()) {
//                                                val userId = document.get("id").toString()
//                                            }
//                                            if (FirebaseAuth.getInstance().currentUser?.email != document.get(
//                                                    "email"
//                                                )
//                                            ) {
//                                                val user = document.toObject(User::class.java)
//                                                userList.add(user!!)
//                                                Log.d(
//                                                    "TAG111",
//                                                    "${document.id} => ${document.data}"
//                                                )
//                                            }
//                                        }
//                                        userList.sortByDescending { it.lastMsgTime }
//
//                                        binding.recViewFollow.layoutManager = LinearLayoutManager(applicationContext)
//                                        binding.recViewFollow.setHasFixedSize(true)
//
//                                        val adapter = UserFollowAdapter(this, userList, object : OnClickFollow {
//                                            override fun onClickUserFollow(pos: Int) {
//                                                name = userList[pos].name.toString()
//                                                getToken(pos)
//
//                                                val okuser = User(
//                                                    userList[pos].profileImg,
//                                                    userList[pos].id,
//                                                    userList[pos].name,
//                                                    userList[pos].email,
//                                                    userList[pos].number,
//                                                    userList[pos].password,
//                                                    userList[pos].lastMsg,
//                                                    userList[pos].lastMsgTime,
//                                                    userList[pos].count,
//                                                    userList[pos].token
//                                                )
//
//                                                FirebaseFirestore.getInstance()
//                                                    .collection(FirebaseAuth.getInstance().currentUser?.email.toString())
//                                                    .document(userList[pos].email.toString())
//                                                    .set(okuser)
//                                                    .addOnSuccessListener {
//                                                        Log.d("TAG11", "User Follow")
//                                                        notificationCheckCondition()
//                                                        push("Follow request")
//                                                    }
//                                                    .addOnFailureListener {
//                                                        Log.d("TAG11", "User not Follow")
//                                                    }
//                                            }
//
//                                        }
//                                        )
//                                        binding.recViewFollow.adapter = adapter
//                                    }
//                                }
//                            }
//                    }else{
//                        listenNewMessage()
//                    }
//
//                }
//            }
//
//    }
    private fun followList() {
        FirebaseFirestore.getInstance()
            .collection("User")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("FollowList")
            .addSnapshotListener { value, error ->
                value?.let {
                    if (!it.isEmpty) {
                        Log.d("listUser",userList2.size.toString())
                        userList2.clear()
                        for (document in it.documents) {
                            if (document.get("email") == FirebaseAuth.getInstance().currentUser?.email.toString()) {
                                val userId = document.get("id").toString()
                            }
                            if (FirebaseAuth.getInstance().currentUser?.email != document.get("email")) {
                                val user = document.toObject(User::class.java)
                                userList2.add(user!!)
                                Log.d("TAG111", "${document.id} => ${document.data}")
                            }
                        }
                        Log.d("TAG111333", "${userList2.map { it.email }}")
                        FirebaseFirestore.getInstance().collection("User")
                            .whereNotIn("email", userList2.map { it.email})
                            .addSnapshotListener { value, error ->
                                value?.let {
                                    if (!it.isEmpty) {
                                        userList.clear()
                                        for (document in it.documents) {
                                            if (document.get("email") == FirebaseAuth.getInstance().currentUser?.email.toString()) {
                                                val userId = document.get("id").toString()
                                            }
                                            if (FirebaseAuth.getInstance().currentUser?.email != document.get(
                                                    "email"
                                                )
                                            ) {
                                                val user = document.toObject(User::class.java)
                                                userList.add(user!!)
                                                Log.d(
                                                    "TAG111",
                                                    "${document.id} => ${document.data}"
                                                )
                                            }
                                        }
                                        userList.sortByDescending { it.lastMsgTime }

                                        binding.recViewFollow.layoutManager = LinearLayoutManager(applicationContext)
                                        binding.recViewFollow.setHasFixedSize(true)

                                        val adapter = UserFollowAdapter(this, userList, object : OnClickFollow {
                                            override fun onClickUserFollow(pos: Int) {
                                                name = userList[pos].name.toString()
                                                getToken(pos)

                                                val okuser = User(
                                                    userList[pos].profileImg,
                                                    userList[pos].id,
                                                    userList[pos].name,
                                                    userList[pos].email,
                                                    userList[pos].number,
                                                    userList[pos].password,
                                                    userList[pos].lastMsg,
                                                    userList[pos].lastMsgTime,
                                                    userList[pos].count,
                                                    userList[pos].token
                                                )

                                                FirebaseFirestore.getInstance()
                                                    .collection("User")
                                                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                                    .collection("FollowList")
                                                    .document(userList[pos].id.toString())
                                                    .set(okuser)
                                                    .addOnSuccessListener {
                                                        Log.d("TAG11", "User Follow")
                                                        notificationCheckCondition()
                                                        push("Follow request")
                                                    }
                                                    .addOnFailureListener {
                                                        Log.d("TAG11", "User not Follow")
                                                    }
                                            }

                                        }
                                        )
                                        binding.recViewFollow.adapter = adapter
                                    }
                                }
                            }
                    }else{
                        listenNewMessage()
                    }

                }
            }

    }

    private fun currentUserName(){
        FirebaseFirestore.getInstance().collection("User").addSnapshotListener { value, error ->
            value?.let {
                if (!it.isEmpty) {
                    userList.clear()
                    for (document in it.documents) {
                        if (FirebaseAuth.getInstance().currentUser?.email == document.get("email")) {
                            val user = document.toObject(User::class.java)
                           mainUser = user?.name.toString()
                        }
                    }


                }
            }
        }
    }

    private fun setRecyclerView(){

    }

    private fun getToken(pos: Int) {
        FirebaseFirestore.getInstance().collection("User").get().addOnSuccessListener {
            for (data in it.documents) {
                if (data.get("id").toString() == userList[pos].id) {
                    Token = data.get("token").toString()
                }
            }
        }
    }

    private fun notificationCheckCondition() {
        notificationViewModel.connectionError.observe(this) {
            when (it) {
                "sending" -> {
//                    Toast.makeText(this, "sending notification", Toast.LENGTH_SHORT).show()
                    Log.d("TAG11", "Sending Notification")
                }

                "sent" -> {
//                    Toast.makeText(this, "notification sent", Toast.LENGTH_SHORT).show()
                    Log.d("TAG11", "Send Notification")
                }

                "error while sending" -> {
//                    Toast.makeText(this, "error while sending", Toast.LENGTH_SHORT).show()
                    Log.d("TAG11", "error while sending")
                }
            }
        }
        notificationViewModel.response.observe(this) {
            if (it.isNotEmpty())
                Log.d(TAG, "Notification in Kotlin: $it ")
        }
    }
    fun push(msg: String) {
        Log.d("OK", Token)
        notificationViewModel
            .sendNotification(
                NotificationModel(
                    Token,
                    Data(mainUser, msg)
                )
            )
    }
}