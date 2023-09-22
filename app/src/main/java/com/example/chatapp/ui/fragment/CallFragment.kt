package com.example.chatapp.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.adapter.CallAdapter
import com.example.chatapp.R
import com.example.chatapp.data.modal.User
import com.example.chatapp.interfacefile.onIteamCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale


class CallFragment : Fragment() {
    private var userList = ArrayList<User>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView:RecyclerView
    private lateinit var context: Context
   private lateinit var number:String
    private lateinit var adapter: CallAdapter
    private var result=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view=inflater.inflate(R.layout.fragment_call, container, false)

        context=view.context
        userList = arrayListOf()
        recyclerView=view.findViewById(R.id.rvViewCAll)
        userList = arrayListOf()
        listenNewMessage()


        val serch=view.findViewById<SearchView>(R.id.serch)

        serch.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
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
            adapter = CallAdapter(context, userList,object: onIteamCall {
            override fun onCalling(pos: Int) {
                if(userList[pos].number!="0"){
                    number=userList[pos].number.toString()
                }
                else{
                    Toast.makeText(context, "no number get", Toast.LENGTH_SHORT).show()
                }

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {

                    // Requesting the permission
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CALL_PHONE), 101)

                } else {
//                    Toast.makeText(context, "Permission already granted", Toast.LENGTH_SHORT).show()
                    val phone_intent = Intent(Intent.ACTION_CALL)
                    phone_intent.data = Uri.parse("tel:$number")
                    startActivity(phone_intent)
                    Log.d("Permission","Permissiono_Granted$number")
                }

            }
        })

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

    private fun listenNewMessage() {
        db.collection("User").addSnapshotListener { value, error ->
            value?.let {
                if (!it.isEmpty) {
                    userList.clear()
                    for (document in it.documents) {
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
    }


//    private fun getListOfPlaces(): ArrayList<User> {
//        val userList = arrayListOf<User>()
//        db.collection("User")
//            .get()
//            .addOnSuccessListener {
//                if (!it.isEmpty) {
//                    for (document in it.documents) {
//                        if(FirebaseAuth.getInstance().currentUser?.email!=document.get("email")){
//                            val user = document.toObject(User::class.java)
//                            userList.add(user!!)
//                            Log.d("TAG111", "${document.id} => ${document.data}")
//                        }
//
//                    }
//                    setAdapter()
//                }
//
//            }
//            .addOnFailureListener { exception ->
//                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
//            }
//
//        return userList
//    }



//    private fun checkpermission() {
//        if(ActivityCompat.checkSelfPermission(context,
//                Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CALL_PHONE),101)
//        }
//    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CALL_PHONE), 101)
        } else {
//            Toast.makeText(context, "Permission already granted", Toast.LENGTH_SHORT).show()
            val phone_intent = Intent(Intent.ACTION_CALL)
            phone_intent.data = Uri.parse("tel:$number")
            startActivity(phone_intent)
            Log.d("Permission","Permissiono_Granted$number")
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                val phone_intent = Intent(Intent.ACTION_CALL)
                phone_intent.data = Uri.parse("tel:$number")
                startActivity(phone_intent)
//                Toast.makeText(context, "CALL Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "CALL Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }



}