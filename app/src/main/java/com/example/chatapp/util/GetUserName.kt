package com.example.chatapp.util

import android.util.Log
import com.example.chatapp.data.modal.Message
import com.example.chatapp.data.modal.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object GetUserName {
    var name:String=""
    fun getName(id:String):String {

        FirebaseFirestore.getInstance().collection("User").addSnapshotListener { value, _ ->
            value?.let {
                if (!it.isEmpty) {
                    for (document in it.documents) {
                        if (id == document.get("email").toString()) {
                            val msg = document.toObject(User::class.java)
                            msg?.let { user ->
                                name=user.name.toString()
                            }
                        }
                    }
                }
            }
        }

        return name
    }
}