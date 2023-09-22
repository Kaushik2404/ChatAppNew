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
import com.example.chatapp.ui.activity.ChatHomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val  channelId="notification_channle"
const val  channelName="com.example.pushnotificationfirebase"

class MyFirebaseMessaging:FirebaseMessagingService(){


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("RefreshToken",token)

    }

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
            return
        }
        NotificationManagerCompat.from(this).notify(1, notification.build());

    }

}