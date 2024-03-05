package com.example.chatapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.adapter.UserGroupAdapter
import com.example.chatapp.data.modal.GroupData
import com.example.chatapp.data.modal.GroupList
import com.example.chatapp.data.modal.User
import com.example.chatapp.databinding.ActivityAddGroupBinding
import com.example.chatapp.databinding.GroupAddDialogBinding
import com.example.chatapp.interfacefile.OnClickGroupAdd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddGroupActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddGroupBinding
    private var userList = ArrayList<User>()
    var groupUserList = mutableListOf<GroupList>()

    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: UserGroupAdapter
    var userId:String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userList = arrayListOf()
        listenNewUser()
        onClick()
    }

    private fun onClick() {
        binding.headerGroup.back.setOnClickListener {
            onBackPressed()
        }

        binding.addGroupBtn.setOnClickListener {
            showAddDialog()
        }
    }
    private fun listenNewUser() {
        db.collection("User")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("FollowList")
            .addSnapshotListener { value, _ ->
                value?.let { it ->
                    userList.clear()
                    if (!it.isEmpty) {
                        for (document in it.documents) {
                            if(document.get("email")== FirebaseAuth.getInstance().currentUser?.email.toString()){
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
                            binding.txview.visibility= View.GONE
                            binding.allUserItemView.visibility= View.VISIBLE
                            setAdapter()
                        }
                        else{
                            binding.txview.visibility= View.VISIBLE
                            binding.allUserItemView.visibility= View.GONE
                        }

                    }else{
                        if(userList.isNotEmpty()){
                            binding.txview.visibility= View.GONE
                            binding.allUserItemView.visibility= View.VISIBLE
                            setAdapter()
                        }
                        else{
                            binding.txview.visibility= View.VISIBLE
                            binding.allUserItemView.visibility= View.GONE
                        }
                    }
                }
            }
    }
    private fun setAdapter(){
        adapter = UserGroupAdapter(this, userList,object :OnClickGroupAdd{
            override fun onClickUserAdd(pos: Int) {
                groupUserList.add(GroupList(userList[pos].id))
                Log.d("TAG",groupUserList.size.toString())
                setVisibility()
            }

            override fun onClickUserRemove(pos: Int) {
                groupUserList.remove(GroupList(userList[pos].id))
                Log.d("TAG",groupUserList.size.toString())
                setVisibility()
            }
        })
        binding.allUserItemView.layoutManager = LinearLayoutManager(this)
        binding.allUserItemView.setHasFixedSize(true)
        binding.allUserItemView.adapter = adapter
    }

    private fun setVisibility(){
        if(groupUserList.size > 0){
            binding.addGroupBtn.visibility=View.VISIBLE
        }
        else{
            binding.addGroupBtn.visibility=View.GONE
        }
    }

    private fun showAddDialog() {
        val alert = AlertDialog.Builder(this)
//        val view = layoutInflater.inflate(R.layout.group_add_dialog, null)
        val view =GroupAddDialogBinding.inflate(layoutInflater)
        alert.setView(view.root)
        alert.setCancelable(true)
        val dialog = alert.create()
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        view.addBtn.setOnClickListener {
            groupUserList.add( GroupList(FirebaseAuth.getInstance().currentUser?.uid))
            val groupId =  FirebaseFirestore.getInstance()
                .collection("User")
                .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .collection("Group List")
                .document()
                .id+"Group"


            val groupData= GroupData(groupId,groupUserList,view.groupNameEdt.text.toString().trim())

            FirebaseFirestore.getInstance()
                .collection("Group List")
                .document(groupId)
                .set(groupData)
                .addOnSuccessListener {
                    Log.d("TAG11", "Group Created")

                    groupUserList.forEach {
                        FirebaseFirestore.getInstance()
                            .collection("User")
                            .document(it.id.toString())
                            .collection("Group List")
                            .document(groupId)
                            .set(GroupList(groupId))
                            .addOnSuccessListener {
                                Log.d("TAG11", "Group Created")
                                dialog.dismiss()
                                onBackPressed()
                            }
                            .addOnFailureListener {
                                Log.d("TAG11", "Group Creates Failed")
                                Toast.makeText(this, "Failed",Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                    }
                    dialog.dismiss()
                    onBackPressed()
                }
                .addOnFailureListener {
                    Log.d("TAG11", "Group Creates Failed")
                    Toast.makeText(this, getString(R.string.failed_group_creation),Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }


        }
        dialog.show()
    }
}