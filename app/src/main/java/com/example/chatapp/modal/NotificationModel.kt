package com.example.chatapp.modal



//  {
//
//    "to":"user device token",
//    "data":{
//    "title" : "hi",
//    "message" : "hello"
//   }
// }



data class NotificationModel(
    val to: String,
    val data: Data,
)

data class Data(
    val title: String,
    val message: String,
)

