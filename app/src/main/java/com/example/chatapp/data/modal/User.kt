package com.example.chatapp.data.modal

data class User(
                var profileImg:String?=null,
                var id:String?=null,
                var name:String?=null,
                var email:String?=null,
                var number:String?=null,
                var password:String?=null,
                var lastMsg:String?="",
                var lastMsgTime:String?="",
                var count:Int?=0,
                var token:String?=null
               )
