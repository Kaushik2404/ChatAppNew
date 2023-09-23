package com.example.chatapp.data.modal

data class NotificationModel(
    val to: String,
    val data: Data,
)

data class Data(
    val title: String,
    val message: String,
)

