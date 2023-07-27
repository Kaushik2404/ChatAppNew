package com.example.chatapp.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.adapter.MesssageAdapter
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.modal.Message
import com.example.chatapp.modal.User
import com.example.chatapp.modell.Data
import com.example.chatapp.modell.NotificationModel
import com.example.chatapp.ui.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    private val db = FirebaseFirestore.getInstance()
    var msgList = ArrayList<Message>()

    private val CAMERA_REQ = 101

    private var reciveRoom: String? = null
    private var sendRoom: String? = null
    private lateinit var msgAdapter: MesssageAdapter
    private lateinit var ImageUri:String
    private lateinit var photo:Uri
    final private var Count = 0
    lateinit var name:String
    lateinit var ID:String
    lateinit var userID:String
    lateinit var Token:String
    lateinit var reciverEmail:String
//    var msgOk=""
    lateinit var currentMsg:String
    lateinit var type:String
//    lateinit var Email:String
    lateinit var launcher: ActivityResultLauncher<String>
    private  val TAG = "Chat"
    var clickCount=0
    private val notificationViewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        msgList = arrayListOf()

        binding.chatBar.chatUserName.text = intent.getStringExtra("NAME")
        ID = intent.getStringExtra("ID").toString()
        userID = intent.getStringExtra("USERID").toString()
        reciverEmail = intent.getStringExtra("UID").toString()

        currentUserName()
        getToken()
        uplodeimage()
        onclick()
        listenNewMessage()
        onClickDilogFile()
    }

    private fun sendCaptureImage() {

        val photoName=getFileName(photo).toString()
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Sending Image")
        mProgressDialog.show()

        val reference: StorageReference =
            FirebaseStorage.getInstance().getReference().child(photoName)
        reference.putFile(photo).addOnSuccessListener {

            val reference1: StorageReference =
                FirebaseStorage.getInstance().getReference().child(photoName)
            reference1.downloadUrl.addOnSuccessListener { uri ->
//
                currentMsg = uri.toString()
                Log.d("Okmsg", currentMsg)

                userMsgAdd("image","image")
                type="image"

                val id = db.collection("Chat_Test").document().id

                val reciverid = intent.getStringExtra("UID").toString()
                val senderid = FirebaseAuth.getInstance().currentUser?.email.toString()

                val calendar: Calendar = Calendar.getInstance()
                val format = SimpleDateFormat(" d MMM yyyy HH:mm:ss ")
                val time: String = format.format(calendar.time)

                val msg = Message(id,currentMsg, senderid, reciverid, time,type)

                db.collection("Chat_Test").document(id).set(msg).addOnCompleteListener {
                    Log.d("result", "ok save photo$type")
                    mProgressDialog.dismiss()
//            msgList.add(msg)
//            msgAdapter.notifyItemInserted(msgList.size - 1)
                }.addOnFailureListener {
                    mProgressDialog.dismiss()
                    Toast.makeText(applicationContext, "Failed..Sending Image", Toast.LENGTH_SHORT).show()
                }

                notificationCheckCondition()
                push("photo")

            }.addOnFailureListener {
                mProgressDialog.dismiss()
                Toast.makeText(applicationContext, "Failed send Image..", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            mProgressDialog.dismiss()
            Toast.makeText(applicationContext, "error sending", Toast.LENGTH_SHORT).show()
        }

    }

    private fun onClickDilogFile() {
        binding.file.setOnClickListener {

        }
        binding.camera.setOnClickListener {
            clickCount=0
            binding.attachFileview.visibility=View.GONE
        if (ContextCompat.checkSelfPermission(this@ChatActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

                // Requesting permission
            ActivityCompat.requestPermissions(this@ChatActivity, arrayOf(Manifest.permission.CAMERA), 101)

            } else {
//                    Toast.makeText(context, "Permission already granted", Toast.LENGTH_SHORT).show()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQ)
                Log.d("Permission","Permissiono_Granted CAMERA")
            }

        }
        binding.galary.setOnClickListener {
            clickCount=0
            binding.attachFileview.visibility=View.GONE
            getImageId()
        }
        binding.location.setOnClickListener {

        }
        binding.audio.setOnClickListener {

        }
        binding.contact.setOnClickListener {

        }
    }
    private fun onclick() {
        binding.btnattachFile.setOnClickListener {
            if(clickCount==0){
                binding.attachFileview.visibility=View.VISIBLE
                clickCount=1
            }
            else{
                binding.attachFileview.visibility=View.GONE
                clickCount=0
            }
        }
        binding.image.setOnClickListener {
            getImageId()
        }
        binding.chatBar.back.setOnClickListener {
            onBackPressed()
        }
        binding.edtMsg.setOnEditorActionListener { v, actionId, event ->

            if (binding.edtMsg.text.toString() != "") {
                if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.sendMsg.performClick()
                }
            }
            false
        }

        binding.edtMsg.setOnClickListener {
            binding.recyclerview.post(Runnable { binding.recyclerview.scrollToPosition(msgList.size - 1) })
        }
        binding.sendMsg.setOnClickListener {
            var msgOk=binding.edtMsg.text.toString()
            if (binding.edtMsg.text.toString().trim() != "") {

                userMsgAdd("msg",binding.edtMsg.text.toString())
                notificationCheckCondition()
                push(msgOk)
                chatDataInsert()

            }
        }

    }

    private fun userMsgAdd(typ:String,currentMsg:String) {
        type=typ
//        val currentMsg = binding.edtMsg.text.toString()
        Count += 1
//                val calendar: Calendar = Calendar.getInstance()
//               val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
//
        val calendar: Calendar = Calendar.getInstance()
        val format = SimpleDateFormat(" d MMM yyyy HH:mm:ss ")
        val time: String = format.format(calendar.time)

        db.collection(FirebaseAuth.getInstance().currentUser?.email.toString()).document(reciverEmail)
            .update("lastMsg", currentMsg, "lastMsgTime", time)
            .addOnSuccessListener {
                Log.d("TAG11", currentMsg)
                Log.d("TAG11", "User id $userID")
                db.collection(reciverEmail).document(FirebaseAuth.getInstance().currentUser?.email.toString()).update(
                    "lastMsg",
                    currentMsg,
                    "lastMsgTime",
                    time,
                    "count",
                    Count
                )
                    .addOnSuccessListener {
                        Log.d(
                            "TAG1111",
                            "update last message" + binding.edtMsg.text.toString()
                        )
                        Log.d("TAG11", "update last message")

                    }
            }

    }
    private fun chatDataInsert() {

        val id = db.collection("Chat_Test").document().id

        val reciverid = intent.getStringExtra("UID").toString()
        val senderid = FirebaseAuth.getInstance().currentUser?.email.toString()

        val calendar: Calendar = Calendar.getInstance()
        val format = SimpleDateFormat(" d MMM yyyy HH:mm:ss ")
        val time: String = format.format(calendar.time)
//        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL).format(time)
//        val id = db.collection("Chat_Test").document().id

        val msg = Message(id, binding.edtMsg.text.toString(), senderid, reciverid, time,type)

        db.collection("Chat_Test").document(id).set(msg).addOnCompleteListener {
//            msgList.add(msg)
//            msgAdapter.notifyItemInserted(msgList.size - 1)
        }
        binding.edtMsg.text.clear()
    }
    private fun getImageId() {
        launcher.launch("image/*")
    }
    private fun uplodeimage() {
        launcher = registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent()
        ) { result ->
            if(result!=null){
                ImageUri = getFileName(result!!).toString()
                val mProgressDialog = ProgressDialog(this)
                mProgressDialog.setMessage("Sending Image")
                mProgressDialog.show()

                val reference: StorageReference =
                    FirebaseStorage.getInstance().getReference().child(ImageUri)
                reference.putFile(result!!).addOnSuccessListener {

                    val reference1: StorageReference =
                        FirebaseStorage.getInstance().getReference().child(ImageUri)
                    reference1.downloadUrl.addOnSuccessListener { uri ->
//
                        currentMsg = uri.toString()
                        Log.d("Okmsg", currentMsg)

                        userMsgAdd("image","image")
                        type="image"

                        val id = db.collection("Chat_Test").document().id

                        val reciverid = intent.getStringExtra("UID").toString()
                        val senderid = FirebaseAuth.getInstance().currentUser?.email.toString()

                        val calendar: Calendar = Calendar.getInstance()
                        val format = SimpleDateFormat(" d MMM yyyy HH:mm:ss ")
                        val time: String = format.format(calendar.time)

                        val msg = Message(id,currentMsg, senderid, reciverid, time,type)

                        db.collection("Chat_Test").document(id).set(msg).addOnCompleteListener {
                            Log.d("result", "ok save photo$type")
                            mProgressDialog.dismiss()
//            msgList.add(msg)
//            msgAdapter.notifyItemInserted(msgList.size - 1)
                        }.addOnFailureListener {
                            mProgressDialog.dismiss()
                            Toast.makeText(applicationContext, "Failed..Sending Image", Toast.LENGTH_SHORT).show()
                        }

                        notificationCheckCondition()
                        push("photo")

                    }.addOnFailureListener {
                        mProgressDialog.dismiss()
                        Toast.makeText(applicationContext, "Failed send Image..", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    mProgressDialog.dismiss()
                    Toast.makeText(applicationContext, "error sending", Toast.LENGTH_SHORT).show()
                }


            }


        }
    }
    private fun getToken() {
        db.collection("User").get().addOnSuccessListener {
            for(data in it.documents){
                if(data.get("id").toString()== ID){
                    Token=data.get("token").toString()
                }
            }
        }
    }
    private fun currentUserName() {

        db.collection("User").addSnapshotListener { value, error ->
            value?.let {
                if (!it.isEmpty) {
                    for (document in it.documents) {
                        if (FirebaseAuth.getInstance().currentUser?.email == document.get("email")) {
                            val user = document.toObject(User::class.java)
                             name = user?.name.toString()
                        }
                    }
                }
            }
        }

    }
    private fun listenNewMessage() {
        db.collection("Chat_Test").addSnapshotListener { value, error ->
            val reciverid = intent.getStringExtra("UID").toString()
            val senderid = FirebaseAuth.getInstance().currentUser?.email.toString()

            value?.let {
                if (!it.isEmpty) {
                    for (document in it.documents) {
                        if (FirebaseAuth.getInstance().currentUser?.email != document.get("email")) {
                            val msg = document.toObject(Message::class.java)
                            msg?.let { msg ->


                                if ((msg?.sendId == senderid && msg?.reciverID == reciverid) || (msg?.reciverID == senderid && msg?.sendId == reciverid)) {
                                    if (msgList.any { it.msgID == msg.msgID }) {

                                    } else {
                                        msgList.add(msg)

                                        if (this::msgAdapter.isInitialized) {

                                            msgAdapter.notifyItemInserted(msgList.size - 1)
                                        } else {


                                        }
                                    }
                                }
                            }
                        }

                    }
                    msgList.sortBy { it.time }
                    setDataRec()
                }
            }
        }
    }
    private fun setDataRec() {
        msgAdapter = MesssageAdapter(this, msgList)
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.adapter = msgAdapter
        binding.recyclerview.post(Runnable { binding.recyclerview.scrollToPosition(msgList.size - 1) })
    }

    override fun onStart() {
        super.onStart()
        val ID = intent.getStringExtra("ID").toString()

        db.collection(FirebaseAuth.getInstance().currentUser?.email.toString()).document(reciverEmail).update("count", Count)
            .addOnSuccessListener {
                Log.d("TAG11", "update last message on start")
            }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        Count = 0
        val ID = intent.getStringExtra("ID").toString()
        db.collection(FirebaseAuth.getInstance().currentUser?.email.toString()).document(reciverEmail).update("count", Count)
            .addOnSuccessListener {
                Log.d("TAG11", "update last message on start")
            }
    }

    private fun notificationCheckCondition() {
        notificationViewModel.connectionError.observe(this){
            when(it){
                "sending"-> {
//                    Toast.makeText(this, "sending notification", Toast.LENGTH_SHORT).show()
                    Log.d("TAG11","Sending Notification")
                }
                "sent"-> {
//                    Toast.makeText(this, "notification sent", Toast.LENGTH_SHORT).show()
                    Log.d("TAG11","Send Notification")
                }
                "error while sending"-> {
//                    Toast.makeText(this, "error while sending", Toast.LENGTH_SHORT).show()
                    Log.d("TAG11","error while sending")
                }
            }
        }

        notificationViewModel.response.observe(this){
            if (it.isNotEmpty())
                Log.d(TAG, "Notification in Kotlin: $it ")
        }
    }
    fun push(msg:String) {
        Log.d("OK",ID)
        Log.d("OK",Token)
        notificationViewModel
                .sendNotification(
                    NotificationModel(
                        Token,
                        Data(name,msg)
                    )
                )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( resultCode == Activity.RESULT_OK) {
            Log.d("IMAGE URI","true")
            if(requestCode == CAMERA_REQ ){
                Log.d("IMAGE URI","true1")
                    val bitphoto = data?.extras?.get("data") as Bitmap
                    getImageUri(this@ChatActivity,bitphoto)
                    Log.d("IMAGE URI",photo.toString())
                    sendCaptureImage()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQ)
            } else {
                Toast.makeText(this@ChatActivity, "CAMERA Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getImageUri(context: Activity, inImage: Bitmap) {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.getContentResolver(),
            inImage,
            "Capture_"+Calendar.getInstance().getTime().toString(),
            null
        )
        photo=Uri.parse(path)
    }


}