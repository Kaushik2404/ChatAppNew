package com.example.chatapp.ui.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.interfacefile.OnIteamClickUser
import com.example.chatapp.R
import com.example.chatapp.adapter.UserAdapter
import com.example.chatapp.ui.activity.chat.ChatActivity
import com.example.chatapp.data.modal.Message
import com.example.chatapp.data.modal.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale


class ChatFragment : Fragment() {
    private var userList = ArrayList<User>()
    private var msgList = ArrayList<Message>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView:RecyclerView
    private lateinit var txview:TextView
    private lateinit var context:Context
    private lateinit var adapter: UserAdapter
    private var result=false
    var userId:String?=null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val view=inflater.inflate(R.layout.fragment_chat, container, false)
        context=view.context
        userList = arrayListOf()
        recyclerView=view.findViewById(R.id.recyclerview)

//        userList = getListOfPlaces()
//        setAdapter()
        try {
            listenNewMessage()
        }catch (e:Exception){
            e.printStackTrace()
            Log.d("Loding","Load error")
        }


        Log.d("TAG11111",userList.size.toString())

        val serch=view.findViewById<SearchView>(R.id.serch)

        txview=view.findViewById(R.id.txview)

        serch.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (result){
                    Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show()
                }
               return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filter(newText)
                }
                return false
            }
        })
        return view
    }
    private fun filter(text: String) {
        val filteredlist = ArrayList<User>()

        for (item in userList) {
            if (item.name?.toLowerCase()?.contains(text.lowercase(Locale.getDefault())) == true) {

                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            filteredlist.clear()
            adapter.filterList(filteredlist)
            result= true
        } else {
            result=false
            adapter.filterList(filteredlist)
        }
    }
    private fun setAdapter(){
        adapter = UserAdapter(context, userList,object: OnIteamClickUser {
            override fun onClickUser(pos: Int) {
                val intent= Intent(context, ChatActivity::class.java)
                intent.putExtra("POS",pos)
                intent.putExtra("UID",userList[pos].email)
                intent.putExtra("NAME",userList[pos].name)
                intent.putExtra("ID",userList[pos].id)
                intent.putExtra("USERID",userId)
                intent.putExtra("profileImage",userList[pos].profileImg)
                startActivity(intent)
            }
            override fun onLongClick(pos: Int) {
                val dialog = Dialog(context)
                dialog.requestWindowFeature(1)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
                dialog.setContentView(R.layout.dialog_exit)
                val textView = dialog.findViewById<TextView>(R.id.tv_yes)
                (dialog.findViewById<View>(R.id.tv_no) as TextView).setOnClickListener { dialog.dismiss() }
                textView.setOnClickListener {
                    db.collection("User")
                        .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                        .collection("FollowList")
                        .document(userList[pos].id.toString())
                        .delete()
                        .addOnSuccessListener {
                            Log.d("TAG11", "DocumentSnapshot successfully deleted!")
                            adapter.notifyItemRemoved(pos)
                        }
                        .addOnFailureListener { e -> Log.w("TAG11", "Error deleting document", e) }
                    dialog.dismiss()
                }
                dialog.show()

            }
        })
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

    //secound
    private fun listenNewMessage() {
        db.collection("User")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("FollowList")
            .addSnapshotListener { value, error ->
            value?.let {
                userList.clear()
                if (!it.isEmpty) {
                    for (document in it.documents) {
                        if(document.get("email")==FirebaseAuth.getInstance().currentUser?.email.toString()){
                            userId=document.get("id").toString()
                        }
                        if(FirebaseAuth.getInstance().currentUser?.email!=document.get("email")){
                            val user = document.toObject(User::class.java)
                            userList.add(user!!)
                            Log.d("TAG111", "${document.id} => ${document.data}")
                        }
                    }
                    userList.sortByDescending { it.lastMsgTime }

                    if(userList.isNotEmpty()){
                        txview.visibility=View.GONE
                        recyclerView.visibility=View.VISIBLE
                        setAdapter()
                    }
                    else{
                        txview.visibility=View.VISIBLE
                        recyclerView.visibility=View.GONE
                    }

                }else{
                    if(userList.isNotEmpty()){
                        txview.visibility=View.GONE
                        recyclerView.visibility=View.VISIBLE
                        setAdapter()
                    }
                    else{
                        txview.visibility=View.VISIBLE
                        recyclerView.visibility=View.GONE
                    }
                }
            }
        }
    }

}