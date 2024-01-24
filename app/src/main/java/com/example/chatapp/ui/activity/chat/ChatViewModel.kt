package com.example.chatapp.ui.activity.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.data.modal.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatViewModel: ViewModel() {


    private val msgLiveData= MutableLiveData<ArrayList<Message>>()
    val msgList : LiveData<ArrayList<Message>>
        get() = msgLiveData


    fun getMsgList(reciverId:String, senderId:String){
        FirebaseFirestore.getInstance().collection("Chat_Test").addSnapshotListener { value, error ->
            value?.let {
                if (!it.isEmpty) {

                    val messagesList = ArrayList<Message>()
                    for (document in it.documents) {
                        if (FirebaseAuth.getInstance().currentUser?.email != document.get("email")) {
                            val msg = document.toObject(Message::class.java)
                            msg?.let { msg ->

                                if ((msg.sendId == senderId && msg.reciverID == reciverId) ||
                                    (msg.reciverID == senderId && msg.sendId == reciverId)) {
                                    messagesList.add(msg)
//                                    msgLiveData.postValue(msg)

                                }
                            }
                        }

                    }
                    msgLiveData.postValue(messagesList)

                }
            }
        }
    }

}
