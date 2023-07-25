package com.example.chatapp.fragment

import android.app.Dialog
import android.content.ContentValues.TAG
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
import com.example.chatapp.OnIteamClickUser
import com.example.chatapp.R
import com.example.chatapp.adapter.UserAdapter
import com.example.chatapp.activity.ChatActivity
import com.example.chatapp.modal.Message
import com.example.chatapp.modal.User
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
        listenNewMessage()

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
        // creating a new array list to filter our data.
        val filteredlist = ArrayList<User>()

        // running a for loop to compare elements.
        for (item in userList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.name?.toLowerCase()?.contains(text.lowercase(Locale.getDefault())) == true) {
                // if the item is matched we are
                // adding it to our filtered list.
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
        if(userList.size==0){
            txview.visibility=View.VISIBLE
            recyclerView.visibility=View.GONE
        }
        else{
            txview.visibility=View.GONE
            recyclerView.visibility=View.VISIBLE

        }

        adapter = UserAdapter(context, userList,object: OnIteamClickUser {
            override fun onClickUser(pos: Int) {
                val intent= Intent(context,ChatActivity::class.java)
                intent.putExtra("POS",pos)
                intent.putExtra("UID",userList[pos].email)
                intent.putExtra("NAME",userList[pos].name)
                intent.putExtra("ID",userList[pos].id)
                intent.putExtra("USERID",userId)
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

                    db.collection(FirebaseAuth.getInstance().currentUser?.email.toString()).document(userList[pos].email.toString())
                        .delete()
                        .addOnSuccessListener {
                            Log.d("TAG11", "DocumentSnapshot successfully deleted!")
//                            adapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { e -> Log.w("TAG11", "Error deleting document", e) }
                    dialog.dismiss()

                }
                dialog.show()

//                val builder = AlertDialog.Builder(context)
//                //set title for alert dialog
//                builder.setTitle("Delete Chat")
//                //set message for alert dialog
//                builder.setMessage("Ary you Confirm Delete this Chat..")
////                builder.setIcon(android.R.drawable.ic_dialog_alert)
//
//                //performing positive action
//                builder.setPositiveButton("Yes"){dialogInterface, which ->
//                    db.collection("User").document(userList[pos].id.toString())
//                        .delete()
//                        .addOnSuccessListener {
//                            Log.d(TAG, "DocumentSnapshot successfully deleted!")
//
//                        }
//                        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
//                }
//                //performing cancel action
////                builder.setNeutralButton("Cancel"){dialogInterface , which ->
//////                    Toast.makeText(applicationContext,"clicked cancel\n operation cancel",Toast.LENGTH_LONG).show()
////                }
//                //performing negative action
//                builder.setNegativeButton("No"){dialogInterface, which ->
//                    adapter.notifyDataSetChanged()
//                }
//                builder.setCancelable(true)
//                // Create the AlertDialog
//                val alertDialog: AlertDialog = builder.create()
//                // Set other dialog properties
//                alertDialog.setCancelable(false)
//                alertDialog.show()


            }
        })
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }
//    private fun getListOfPlaces(): ArrayList<User> {
//        val userList = arrayListOf<User>()
//        db.collection("User")
//            .get()
//            .addOnSuccessListener {
//                if (!it.isEmpty) {
//                    userList.clear()
//                    for (document in it.documents) {
//                        if(document.get("email")==FirebaseAuth.getInstance().currentUser?.email.toString()){
//
//                            userId=document.get("id").toString()
//                        }
//                        if(FirebaseAuth.getInstance().currentUser?.email!=document.get("email")){
//                            val user = document.toObject(User::class.java)
//                            userList.add(user!!)
//
//                            Log.d("TAG111", "${document.id} => ${document.data}")
//                        }
//                    }
//                    userList.sortByDescending { it.lastMsgTime }
//                    setAdapter()
//                }
//
//            }
//
//            .addOnFailureListener { exception ->
//                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
//            }
//
//        return userList
//    }

    private fun listenNewMessage() {
        db.collection(FirebaseAuth.getInstance().currentUser?.email.toString()).addSnapshotListener { value, error ->
            value?.let {
                if (!it.isEmpty) {
                    userList.clear()
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
                    setAdapter()
                }
            }
        }

//        db.collection("User").addSnapshotListener { value, error ->
//            value?.let {
//                if (!it.isEmpty) {
//                    userList.clear()
//                    for (document in it.documents) {
//                        if(document.get("email")==FirebaseAuth.getInstance().currentUser?.email.toString()){
//                            userId=document.get("id").toString()
//                        }
//                        if(FirebaseAuth.getInstance().currentUser?.email!=document.get("email")){
//                            val user = document.toObject(User::class.java)
//                            userList.add(user!!)
//                            Log.d("TAG111", "${document.id} => ${document.data}")
//                        }
//                    }
//                    userList.sortByDescending { it.lastMsgTime }
//                    setAdapter()
//                }
//            }
//        }
    }

}