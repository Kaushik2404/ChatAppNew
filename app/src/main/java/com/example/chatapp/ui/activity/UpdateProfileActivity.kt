package com.example.chatapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.databinding.ActivityUpdateProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdateProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityUpdateProfileBinding
    val db=FirebaseFirestore.getInstance()
    lateinit var Id:String
    lateinit var PASS:String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityUpdateProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Id=intent.getStringExtra("ID").toString()
        PASS=intent.getStringExtra("PASS").toString()

        onSetData()
        clickEvent()

    }

    private fun onSetData() {
        binding.updateEmail.setText(intent.getStringExtra("EMAIL").toString())
        binding.updateNumber.setText(intent.getStringExtra("NUMBER").toString())
        binding.updateName.setText(intent.getStringExtra("NAME").toString())
    }

    private fun clickEvent() {
        binding.update.setOnClickListener {
            if(error()){
                updateData()
            }

        }
        binding.updateHeader.back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun updateData() {


        db.collection("User").document(Id).update("name",binding.updateName.text.toString(),"email",binding.updateEmail.text.toString(),
            "number",binding.updateNumber.text.toString())
            .addOnSuccessListener {
                Log.d("TAG1111","update last message")
                if(binding.oldPassword.text.toString()==PASS){
                    
                    FirebaseAuth.getInstance().currentUser?.updatePassword(binding.UpdatePassword.text.toString())
                    db.collection("User").document(Id).update("password",binding.UpdatePassword.text.toString())
                    onBackPressed()
                }
                else{
                    Toast.makeText(applicationContext, " old password correct Type", Toast.LENGTH_SHORT).show()
                }


            }
    }
    private fun  error():Boolean{

        if(binding.updateName.text.isEmpty()){
            binding.updateName.error="Enter name"
            return false
        }


        if(binding.updateEmail.text.isEmpty()){
            binding.updateEmail.error="Enter Email"
            return false
        }
        if(!(binding.updateEmail.text.toString().endsWith("@gmail.com"))){
            binding.updateEmail.error="email formate please correct"
            return false
        }

        if(binding.updateNumber.text.isEmpty()){
            binding.updateNumber.error="Enter number"
            return false
        }
        if(binding.oldPassword.text.isEmpty()){
            binding.oldPassword.error="Enter oldPassword"
            return false
        }
        if(binding.oldPassword.text.toString().length<6){
            binding.oldPassword.error="minimum 6 character enter"
            return false
        }
        if(binding.UpdatePassword.text.isEmpty()){
            binding.UpdatePassword.error="Enter new Password"
            return false
        }
        if(binding.UpdatePassword.text.toString().length<6){
            binding.UpdatePassword.error="minimum 6 character enter"
            return false
        }

        if(binding.updateNumber.text.length!=10){
            binding.updateNumber.error="Number is 10 digit"
            return false
        }


        return true

    }

}