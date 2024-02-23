package com.example.chatapp.ui.activity

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.chatapp.databinding.ActivitySignupBinding
import com.example.chatapp.data.modal.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var launcher: ActivityResultLauncher<String>

     private var imageUri:String=""
    private lateinit var imageUriok:Uri
    private lateinit var okUri:String
    private lateinit var token:String

     val auth:FirebaseAuth=FirebaseAuth.getInstance()
     private var db=FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uplodeimage()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // fetching the token
             token = task.result
        })
        binding.profileImage.setOnClickListener{
            getImageId()
        }


        binding.signup.setOnClickListener {
            if(checkError()){
                if(imageUri!=""){
                    val dialog = ProgressDialog(this)
                    dialog.setMessage("Login...")
                    dialog.show()
                    auth.createUserWithEmailAndPassword(binding.email.text.toString(),binding.password.text.toString())
                        .addOnCompleteListener { task->
                            if(task.isSuccessful){

                                val reference1: StorageReference =
                                    FirebaseStorage.getInstance().reference.child("Profile").child(imageUri)
                                reference1.downloadUrl.addOnSuccessListener { uri ->
                                    okUri=uri.toString()
                                    Log.d("uri",okUri)

                                    val userId=FirebaseAuth.getInstance().currentUser?.uid.toString()
                                    val user= User(okUri,
                                        userId,
                                        binding.name.text.toString(),
                                        binding.email.text.toString(),
                                        binding.number.text.toString(),
                                        binding.password.text.toString(),
                                        "",
                                        "",
                                        0,token)

                                    db.collection("User").document(userId)
                                        .set(user)
                                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!")

                                            Toast.makeText(applicationContext, "Sign in Successfully", Toast.LENGTH_SHORT).show()
                                            dialog.dismiss()
                                            val intent=Intent(applicationContext, ChatHomeActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e)
                                            Toast.makeText(applicationContext, "failed save the data", Toast.LENGTH_SHORT).show()
                                        }

                                }.addOnFailureListener {
                                    Toast.makeText(this, "Profile pic add", Toast.LENGTH_SHORT).show()
                                }

//                            val userId=db.collection("User").document().id

                            }
                            else{
                                binding.name.setText("")
                                binding.email.setText("")
                                binding.number.setText("")
                                binding.password.setText("")
                                Toast.makeText(applicationContext, "Sign in Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }else{
                    Toast.makeText(this, "profile Pic add", Toast.LENGTH_SHORT).show()
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
            val intent=Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }

    }


    private fun getImageId() {
        launcher.launch("image/*")
    }
    private fun uplodeimage() {
        launcher = registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent()
        ) { result ->
            if(result!=null){
                imageUri = getFileName(result).toString()
                Glide.with(this).load(result)
                    .into(binding.profileImage)

                val reference: StorageReference =
                    FirebaseStorage.getInstance().reference.child("Profile").child(imageUri)
                reference.putFile(result).addOnSuccessListener {
                    Log.d("Image","sendInFirebase")
                }.addOnFailureListener {
                    Log.d("Image","Failed")
                }

                }
        }
    }

    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
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