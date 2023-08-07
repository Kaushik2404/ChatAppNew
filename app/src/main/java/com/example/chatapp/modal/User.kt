package com.example.chatapp.modal

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
               ){

//    fun getName(): String? {
//        return name
//    }
//
//    fun setName(courseName: String) {
//        this.name = courseName
//    }

}

