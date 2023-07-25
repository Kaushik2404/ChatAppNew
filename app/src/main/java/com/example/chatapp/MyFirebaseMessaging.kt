package com.example.chatapp

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.chatapp.activity.ChatHomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val  channelId="notification_channle"
const val  channelName="com.example.pushnotificationfirebase"

class MyFirebaseMessaging:FirebaseMessagingService(){



    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("RefreshToken",token)

    }




//    override fun onMessageReceived(remoteMessage: RemoteMessage){
////       if(remoteMessage.notification !=null){
//
////           genrateNotification(remoteMessage.notification!!.title!!,remoteMessage.notification!!.body!!)
//
//
//           String title =remoteMessage.getNotification().getTitle();
//           String text = remoteMessage.getNotification().getBody();
//
//           final String CHANNEL_ID = "HEADS_UP_NOTIFICATION";
//
//           NotificationChannel channel = new NotificationChannel(
//               CHANNEL_ID,
//               "Heads Up Notification",
//               NotificationManager.IMPORTANCE_HIGH
//
//
//        super.onMessageReceived(remoteMessage);
////       }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        var title:String=message.notification?.title.toString()
        var text:String=message.notification?.body.toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val Channel= NotificationChannel(channelId,"notification_channle", NotificationManager.IMPORTANCE_HIGH)
        }
        val notification: Notification.Builder = Notification.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this).notify(1, notification.build());

    }



//    @SuppressLint("RemoteViewLayout")
//    fun getRemoteView(title: String, message: String):RemoteViews {
//        val remoteView =RemoteViews( "com.example.chatapp",R.layout.notification)
//          remoteView.setTextViewText(R.id.title,title)
//          remoteView.setTextViewText(R.id.detail,message)
//        remoteView.setImageViewResource(R.id.logo,R.drawable.)
//
//        return remoteView
//    }

//    fun genrateNotification(title:String,message:String){
//        val intent=Intent(this,ChatHomeActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
////        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
//
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//
//
//        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder = NotificationCompat.Builder(this)
//            .setSmallIcon(R.drawable)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setAutoCancel(true)
//            .setSound(soundUri)
//            .setContentIntent(pendingIntent)
//
//        val notificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            val notificationChanlle =NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_HIGH)
//            notificationManager.createNotificationChannel(notificationChanlle)
//        }
//
//        notificationManager.notify(0,notificationBuilder.build())
//
//

//    }

}