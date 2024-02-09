package com.example.chatapp.util

import android.util.Log
import com.example.chatapp.data.modal.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GetUserName() {
    var name:String=""
    fun getName(id:String):String {
        FirebaseFirestore.getInstance().collection("User")
            .addSnapshotListener { value, _ ->
                value?.let {
                    if (!it.isEmpty) {
                        for (document in it.documents) {
                            if (document.get("id") == id) {
                                name = document.get("name").toString()
                            }
                        }
                    }
                }
            }
        return name
    }
}