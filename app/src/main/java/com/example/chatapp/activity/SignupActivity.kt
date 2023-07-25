package com.example.chatapp.activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import com.example.chatapp.databinding.ActivitySignupBinding
import com.example.chatapp.modal.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
     val auth:FirebaseAuth=FirebaseAuth.getInstance()
     var db=FirebaseFirestore.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signup.setOnClickListener {
            if(checkError()){

                auth.createUserWithEmailAndPassword(binding.email.text.toString(),binding.password.text.toString())
                    .addOnCompleteListener { task->
                        if(task.isSuccessful){
//                             val userId=db.collection("User").document().id
                             val userId=FirebaseAuth.getInstance().currentUser?.uid.toString()
                            val user=User(userId,binding.name.text.toString(),binding.email.text.toString(),binding.number.text.toString(),binding.password.text.toString())

                            db.collection("User").document(userId)
                                .set(user)
                                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!")
                                    Toast.makeText(applicationContext, "Sign in Successfully", Toast.LENGTH_SHORT).show()
                                    val intent=Intent(applicationContext,ChatHomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e)
                                    Toast.makeText(applicationContext, "failed save the data", Toast.LENGTH_SHORT).show()
                                }

                        }
                        else{
                            binding.name.setText("")
                            binding.email.setText("")
                            binding.number.setText("")
                            binding.password.setText("")
                            Toast.makeText(applicationContext, "Sign in Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

            }
        }

        binding.password.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().length>=6){
                    binding.check.visibility= View.VISIBLE
                }
                else{
                    binding.check.visibility= View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }

        }
        )


        binding.login.setOnClickListener {
            val intent=Intent(applicationContext,LoginActivity::class.java)
            startActivity(intent)
            finish()

        }



    }

    private fun checkError(): Boolean {

        if(binding.name.text.toString().isEmpty()){
            binding.name.error="enter name please"
            return false
        }

        if(binding.number.text.toString().isEmpty()){
            binding.name.error="enter number please"
            return false
        }
        if(binding.number.text.toString().length!=10){
            binding.name.error="number is 10 digit enter"
            return false
        }

        if(binding.email.text.toString().isEmpty()){
            binding.email.error="enter email please"
            return false
        }
        if(!(binding.email.text.toString().endsWith("@gmail.com"))){
            binding.email.error="email formate please correct"
            return false
        }
       if(binding.password.text.toString().isEmpty()){
            binding.password.error="enter password please"
            return false
        }
        if(binding.password.text.toString().length<6){
            binding.password.error="minimum 6 character enter"
            return false
        }
        return true

    }
}