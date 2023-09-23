package com.example.chatapp.ui.activity

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
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CALL_PHONE
import android.Manifest.permission.CAMERA
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_CONTACTS
import android.Manifest.permission.READ_SMS
import android.util.Log


class IntroActivity : AppCompatActivity() {


    companion object {

        private const val PERMISSION_GRANTED=0
        private const val PERMISSION_DENIED=-1

    }
   private lateinit var binding: ActivityIntroBinding
   private val auth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.login.setOnClickListener {
            val intent=Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        if(auth.currentUser!=null){
            val intent=Intent(applicationContext, ChatHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.signup.setOnClickListener {
            val intent=Intent(applicationContext, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

            if(checkper()){
                Log.d("TAG","---------all permisssion Granted")
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION,CAMERA,CALL_PHONE,READ_SMS,READ_CONTACTS,POST_NOTIFICATIONS),200)
            }
    }

    private  fun checkper():Boolean{
      var resultLocation :Int = ActivityCompat.checkSelfPermission(this,ACCESS_FINE_LOCATION)

      var resultCamera :Int = ActivityCompat.checkSelfPermission(this,CAMERA)
      var resultCall :Int = ActivityCompat.checkSelfPermission(this,CALL_PHONE)
      var resultSms :Int = ActivityCompat.checkSelfPermission(this,READ_SMS)
      var resultContact :Int = ActivityCompat.checkSelfPermission(this,READ_CONTACTS)

        return resultLocation==PackageManager.PERMISSION_GRANTED &&
                resultCamera==PackageManager.PERMISSION_GRANTED &&
                resultCall==PackageManager.PERMISSION_GRANTED &&
                resultSms==PackageManager.PERMISSION_GRANTED &&
                resultContact==PackageManager.PERMISSION_GRANTED

    }

//    private fun checkPermission(permission: String, requestCode: Int) {
//        if (ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_DENIED) {
//
//            // Requesting the permission
//            ActivityCompat.requestPermissions(this@IntroActivity, arrayOf(permission), requestCode)
//        } else {
////            Toast.makeText(applicationContext, "Permission already granted", Toast.LENGTH_SHORT).show()
//        }
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==200){
            if(grantResults.isNotEmpty()){
                var loc:Int=grantResults[0]
                var cam:Int=grantResults[1]
                var call:Int=grantResults[2]
                var sms:Int=grantResults[3]
                var contact:Int=grantResults[4]
//                var notification:Int=grantResults[5]

                 var checkloc :Boolean =loc==PackageManager.PERMISSION_GRANTED
                 var checkcam :Boolean=cam==PackageManager.PERMISSION_GRANTED
                 var checkcall :Boolean=call==PackageManager.PERMISSION_GRANTED
                 var checksms :Boolean=sms==PackageManager.PERMISSION_GRANTED
                 var checkcontact :Boolean=contact==PackageManager.PERMISSION_GRANTED

                if(checkloc && checkcam && checkcall && checksms && checkcontact ){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "denied", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
}