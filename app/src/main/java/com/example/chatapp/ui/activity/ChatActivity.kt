package com.example.chatapp.ui.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.chatapp.R
import com.example.chatapp.adapter.FileDialog
import com.example.chatapp.adapter.MesssageAdapter
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.interfacefile.OnClickDilogFile
import com.example.chatapp.data.modal.Contact
import com.example.chatapp.data.modal.Message
import com.example.chatapp.data.modal.User
import com.example.chatapp.data.modal.Data
import com.example.chatapp.data.modal.NotificationModel
import com.example.chatapp.interfacefile.onClickMsg
import com.example.chatapp.ui.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar


@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    private val db = FirebaseFirestore.getInstance()
    var msgList = ArrayList<Message>()

    private val CAMERA_REQ = 101
    private val PDF_REQ = 102

    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100

    lateinit var bottomSheet:FileDialog
    private lateinit var msgAdapter: MesssageAdapter
    private lateinit var ImageUri:String
    private lateinit var photo:Uri
    lateinit var name:String
    lateinit var ID:String
    lateinit var userID:String
    lateinit var Token:String
    lateinit var reciverEmail:String
    lateinit var currentMsg:String
    lateinit var type:String
    lateinit var launcher: ActivityResultLauncher<String>
   lateinit var data:ArrayList<Contact>
   lateinit var profileImage:String

    private var Count = 0
    private  val TAG = "Chat"
    private val notificationViewModel: NotificationViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        msgList = arrayListOf()
        data= arrayListOf()

        binding.chatBar.chatUserName.text = intent.getStringExtra("NAME")
        ID = intent.getStringExtra("ID").toString()
        userID = intent.getStringExtra("USERID").toString()
        reciverEmail = intent.getStringExtra("UID").toString()
        profileImage = intent.getStringExtra("profileImage").toString()

        currentUserName()
        getToken()
        uplodeimage()
        onclick()
        listenNewMessage()
        setProfileImage()

    }

    private fun setProfileImage() {

        if(profileImage!=null){
        Glide.with(this).load(profileImage)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    binding.chatBar.progrsschat.visibility=View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    binding.chatBar.progrsschat.visibility=View.GONE
                    return false
                }

            })
            .into(binding.chatBar.profilePicChat)
    }else{
            binding.chatBar.progrsschat.visibility=View.GONE
        binding.chatBar.profilePicChat.setImageResource(R.drawable.profile)

    }
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

//                val calendar: Calendar = Calendar.getInstance()
//                val format = SimpleDateFormat(" d MMM yyyy HH:mm:ss ")
//                val time: String = format.format(calendar.time)

                val currentTimestamp = System.currentTimeMillis()
                val msg = Message(id,currentMsg, senderid, reciverid, currentTimestamp.toString(),type)

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
    private fun onclick() {
        binding.btnattachFile.setOnClickListener {

             bottomSheet = FileDialog(object :OnClickDilogFile {
                override fun onClickGalary() {
                    getImageId()
                }
                override fun onClickCamera() {
                    if (ContextCompat.checkSelfPermission(this@ChatActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

                        // Requesting permission
                     ActivityCompat.requestPermissions(this@ChatActivity, arrayOf(Manifest.permission.CAMERA), 101)

                 } else {
                         Toast.makeText(applicationContext, "Permission already granted", Toast.LENGTH_SHORT).show()
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(cameraIntent, CAMERA_REQ)
                        Log.d("Permission","Permissiono_Granted CAMERA")
                        }
                }

                override fun onClickFile() {
                   uplodePdf()

                }

                override fun onClickAudio() {
                    TODO("Not yet implemented")
                }

                override fun onClickLocation() {
                    TODO("Not yet implemented")
                }

                override fun onClickContact() {

                    binding.chatView.visibility=View.GONE
                    binding.contactView.visibility=View.VISIBLE
                    setContactView()
                }

            })

            bottomSheet.show(supportFragmentManager, "ModalBottomSheet")

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

    private fun uplodePdf() {
        val pdfIntent = Intent()
        pdfIntent.action = Intent.ACTION_GET_CONTENT
        pdfIntent.type = "application/pdf"
        startActivityForResult(pdfIntent,PDF_REQ )
    }

    private fun setContactView() {
        bottomSheet.dismiss()
        binding.contactBar.back.setOnClickListener {
            binding.contactView.visibility=View.GONE
            binding.chatView.visibility=View.VISIBLE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf<String>(android.Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            getContacts()
        }


    }

    fun getContacts() {

        // create cursor and query the data
        var cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        startManagingCursor(cursor)

        // data is a array of String type which is
        // used to store Number ,Names and id.
        val data = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone._ID
        )
        val to = intArrayOf(android.R.id.text1, android.R.id.text2)
        // creation of adapter using SimpleCursorAdapter class
        val adapter = SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, data, to)
//        // Calling setAdaptor() method to set created adapter
        binding.contactListView.setAdapter(adapter)
        binding.contactListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE)

        binding.contactListView.setOnItemClickListener { parent, view, position, id ->


            val okNumber = cursor!!.getString(cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val okName = cursor!!.getString(cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))


            currentMsg = okName+"/"+okNumber
            Log.d("Okmsg", currentMsg)


            userMsgAdd("contact","Contact number")
            type="contact"

            val id = db.collection("Chat_Test").document().id

            val reciverid = intent.getStringExtra("UID").toString()
            val senderid = FirebaseAuth.getInstance().currentUser?.email.toString()
//            val calendar: Calendar = Calendar.getInstance()
//            val format = SimpleDateFormat(" d MMM yyyy HH:mm:ss")
//            val time: String = format.format(calendar.time)

            val currentTimestamp = System.currentTimeMillis()
            val msg = Message(id,currentMsg, senderid, reciverid, currentTimestamp.toString(),type)
            db.collection("Chat_Test").document(id).set(msg).addOnCompleteListener {
                Toast.makeText(applicationContext, "Sending Contact", Toast.LENGTH_SHORT).show()
                binding.contactView.visibility= View.GONE
                binding.chatView.visibility= View.VISIBLE

                notificationCheckCondition()
                push("Contact...")
            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Failed..Sending Contact", Toast.LENGTH_SHORT).show()
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
        db.collection("User")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("FollowList")
            .document(ID)
            .update("lastMsg", currentMsg, "lastMsgTime", time)
            .addOnSuccessListener {
                Log.d("TAG11", currentMsg)
                Log.d("TAG11", "User id $userID")
                db.collection("User")
                    .document(ID)
                    .collection("FollowList")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .update(
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

        val currentTimestamp = System.currentTimeMillis()
//        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL).format(time)
//        val id = db.collection("Chat_Test").document().id

        val msg = Message(id, binding.edtMsg.text.toString(), senderid, reciverid, currentTimestamp.toString(),type)
        db.collection("Chat_Test").document(id).set(msg).addOnCompleteListener {
//            msgList.add(msg)
//            msgAdapter.notifyItemInserted(msgList.size - 1)
        }
        binding.edtMsg.text.clear()
    }
    private fun getImageId() {

        launcher.launch("image/*")
        bottomSheet.dismiss()
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

//                        val calendar: Calendar = Calendar.getInstance()
//                        val format = SimpleDateFormat(" d MMM yyyy HH:mm:ss ")
//                        val time: String = format.format(calendar.time)

                        val currentTimestamp = System.currentTimeMillis()

                        val msg = Message(id,currentMsg, senderid, reciverid, currentTimestamp.toString(),type)

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

                                if ((msg.sendId == senderid && msg.reciverID == reciverid) || (msg.reciverID == senderid && msg.sendId == reciverid)) {
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
//
                    }
                    msgList.sortBy { it.time }
                    Log.d("msglist",msgList.toString())
                    setDataRec()
                }
            }
        }
    }
    private fun setDataRec() {
        msgAdapter = MesssageAdapter(this, msgList, object : onClickMsg {

            override fun onLongClickMsg(pos: Int) {


                val builder= AlertDialog.Builder(this@ChatActivity)
                builder.setCancelable(true)
                builder.setIcon(R.drawable.baseline_delete_24)
                builder.setTitle("Delete Data !")
                builder.setMessage("Are you Confirm Delete this Data....")
                builder.setPositiveButton("yes"){dialog, which->

                    deleteMsgView(msgList[pos].msgID.toString(),pos)

//                    msgList.removeAt(pos)
//                    msgAdapter.notifyItemRemoved(pos)
                }
                builder.setNegativeButton("NO"){dialog,which->
                    dialog.dismiss()
                }
                val dialog=builder.create()
                dialog.show()
//                msgList.removeAt(pos)
//                msgAdapter.notifyItemRemoved(pos)
//                performOptionsMenuClick(pos)
                }

            override fun onClickMsg(pos: Int) {
//                binding.PdfView.visibility=View.VISIBLE
//                binding.chatView.visibility=View.GONE
//                binding.contactView.visibility=View.GONE
                val pdfPath = msgList[pos].msg?.toUri()
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(pdfPath, "application/pdf")
                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Log.d("PDFVIEW","ok VIEW PDF ")
                }
            }
        })
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.adapter = msgAdapter
//        binding.recyclerview.post(Runnable { binding.recyclerview.scrollToPosition(msgList.size - 1) })
        binding.recyclerview.scrollToPosition(msgList.size-1)
    }

    private fun updateMsgView(msgId:String,pos: Int) {
        FirebaseFirestore.getInstance().collection("Chat_Test").document(msgId).update("view","OF")
            .addOnSuccessListener {
                Log.d("TAGID", "update view$msgId,of")
                msgList.removeAt(pos)
                msgAdapter.notifyItemRemoved(pos)
            }
    }
    private fun deleteMsgView(msgId:String,pos: Int) {
        FirebaseFirestore.getInstance().collection("Chat_Test").document(msgId).delete()
            .addOnSuccessListener {
                Log.d("TAGID", "Delete view$msgId,of")
                msgList.removeAt(pos)
                msgAdapter.notifyItemRemoved(pos)
            }
    }

    override fun onStart() {
        super.onStart()
        val ID = intent.getStringExtra("ID").toString()

        db.collection("User")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("FollowList")
            .document(ID).update("count", Count)
            .addOnSuccessListener {
                Log.d("TAG11", "update last message on start")
            }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        Count = 0
        val ID = intent.getStringExtra("ID").toString()

        val calendar: Calendar = Calendar.getInstance()
        val format = SimpleDateFormat(" d MMM yyyy HH:mm:ss ")
        val time: String = format.format(calendar.time)

        db.collection("User")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .collection("FollowList")
            .document(ID)
            .update("lastMsgTime",time)
            .addOnSuccessListener {
                Log.d("TAG11", "update last message on back empty")
            }

        if(msgList.isEmpty()){
            db.collection("User")
                .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .collection("FollowList")
                .document(ID)
                .update("lastMsg","","count", Count)
                .addOnSuccessListener {
                    Log.d("TAG11", "update last message on back empty")
                }
        }else{
            if(msgList[msgList.size-1].type.toString()=="image"){
                db.collection("User")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .collection("FollowList")
                    .document(ID)
                    .update("lastMsg","image","count", Count)
                    .addOnSuccessListener {
                        Log.d("TAG11", "update last message on back image")
                    }
            }
            else if(msgList[msgList.size-1].type.toString()=="contact"){
                db.collection("User")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .collection("FollowList")
                    .document(ID)
                    .update("lastMsg","Contact Number","count", Count)
                    .addOnSuccessListener {
                        Log.d("TAG11", "update last message on back contact")
                    }
            }
            else if(msgList[msgList.size-1].type.toString()=="PDF"){
                db.collection("User")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .collection("FollowList")
                    .document(ID)
                    .update("lastMsg","PDF","count", Count)
                    .addOnSuccessListener {
                        Log.d("TAG11", "update last message on back PDF")
                    }
            }
            else{
                db.collection("User")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .collection("FollowList")
                    .document(ID)
                    .update("lastMsg",msgList[msgList.size-1].msg.toString(),"count", Count)
                    .addOnSuccessListener {
                        Log.d("TAG11", "update last message on back")
                    }
            }
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
                        bottomSheet.dismiss()
                    sendCaptureImage()
            }
            if(requestCode == PDF_REQ ){
                sendPdfINDatabase(data)


            }
        }
    }

    private fun sendPdfINDatabase(data: Intent?) {
        Log.d("PDF","true")
        val dialog = ProgressDialog(this)
        dialog.setMessage("Uploading")
        dialog.show()
        val pdfuri = data!!.data
        val timestamp = "" + System.currentTimeMillis()
        val storageReference = FirebaseStorage.getInstance().reference
//        Toast.makeText(this@ChatActivity, pdfuri.toString(),Toast.LENGTH_SHORT).show()
        val okPdf=getFileName(pdfuri!!)
        val filepath = storageReference.child("PDF").child(okPdf.toString())
//        Toast.makeText(this@ChatActivity,okPdf.toString() , Toast.LENGTH_SHORT).show()
        filepath.putFile(pdfuri!!).addOnSuccessListener {
            Log.d("PDF","Store Sucessfully")

            val reference1: StorageReference =
                FirebaseStorage.getInstance().getReference().child("PDF").child(okPdf.toString())
            reference1.downloadUrl.addOnSuccessListener { uri ->
//
                currentMsg = uri.toString()
                Log.d("Okmsg", currentMsg)

                userMsgAdd("PDF","PDF")
                type="PDF"

                val id = db.collection("Chat_Test").document().id

                val reciverid = intent.getStringExtra("UID").toString()
                val senderid = FirebaseAuth.getInstance().currentUser?.email.toString()

//                val calendar: Calendar = Calendar.getInstance()
//                val format = SimpleDateFormat(" d MMM yyyy HH:mm:ss ")
//                val time: String = format.format(calendar.time)

                val currentTimestamp = System.currentTimeMillis()

                val msg = Message(id,currentMsg, senderid, reciverid, currentTimestamp.toString(),type)

                db.collection("Chat_Test").document(id).set(msg).addOnCompleteListener {
                    Log.d("result", "ok save PDF$type")
                    dialog.dismiss()
//            msgList.add(msg)
//            msgAdapter.notifyItemInserted(msgList.size - 1)
                }.addOnFailureListener {
                    dialog.dismiss()
                    Toast.makeText(applicationContext, "Failed..Sending PDF", Toast.LENGTH_SHORT).show()
                }
                notificationCheckCondition()
                push("PDF")

            }.addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(applicationContext, "Failed send PDF..", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }.addOnFailureListener {
            Log.d("PDF","Store Failed")
//            Toast.makeText(this@ChatActivity, "pdf Store Failed ", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
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

        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
//                showContacts()
                getContacts()
            } else {
                Toast.makeText(
                    this,
                    "Until you grant the permission, we canot display the names",
                    Toast.LENGTH_SHORT
                ).show()
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