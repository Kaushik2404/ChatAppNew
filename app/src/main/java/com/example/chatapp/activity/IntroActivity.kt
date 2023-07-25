package com.example.chatapp.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatapp.databinding.ActivityIntroBinding
import com.google.firebase.auth.FirebaseAuth
import android.Manifest



class IntroActivity : AppCompatActivity() {


    companion object {
        private const val INTERNET_PERMISSION = 100
        private const val POST_NOTI = 101

        private const val CALL = 102

    }
   private lateinit var binding: ActivityIntroBinding
   private val auth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.login.setOnClickListener {
            val intent=Intent(applicationContext,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        if(auth.currentUser!=null){
            val intent=Intent(applicationContext,ChatHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.signup.setOnClickListener {
            val intent=Intent(applicationContext,SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

            checkPermission(
                Manifest.permission.CALL_PHONE,CALL
            )
            checkPermission(
            Manifest.permission.POST_NOTIFICATIONS,
            INTERNET_PERMISSION)


    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@IntroActivity, arrayOf(permission), requestCode)
        } else {
//            Toast.makeText(applicationContext, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == POST_NOTI) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@IntroActivity, "Notification Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@IntroActivity, "Notification Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        else if (requestCode == CALL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@IntroActivity, "CALL Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@IntroActivity, "CALL Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}