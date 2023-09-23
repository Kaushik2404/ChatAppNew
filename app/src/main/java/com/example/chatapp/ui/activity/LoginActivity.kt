package com.example.chatapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chatapp.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private val db = FirebaseFirestore.getInstance()
    var id=""


    var auth:FirebaseAuth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.signup.setOnClickListener {
            val intent= Intent(applicationContext, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.login.setOnClickListener {


            if(checkerror()){
                db.collection("User").get().addOnSuccessListener {
                    for (document in it.documents){
                        if(document.get("email").toString()== binding.email.text.toString()){
                            id=document.get("id").toString()
                            addtoken(id)
                        }
                    }
                }
                  auth.signInWithEmailAndPassword(binding.email.text.toString(),binding.password.text.toString())
                      .addOnCompleteListener { task->
                            if(task.isSuccessful){
                                val intent=Intent(applicationContext, ChatHomeActivity::class.java)
                                startActivity(intent)
                                finish()
                             }
                    else{
                        Toast.makeText(applicationContext, "Failed Login ", Toast.LENGTH_SHORT).show()
                    }
                }
             }
        }
    }

    private fun addtoken(id: String) {
        Log.d("TAGID",id)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // fetching the token
            val token = task.result
            db.collection("User").document(id).update("token",token.toString())
                .addOnSuccessListener {
                    Log.d("TAGID", "update Token$token")
                }
        })

    }
    private fun checkerror(): Boolean {

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
        return true
    }
}