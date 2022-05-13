package com.example.final_project_android_version

data class User(var id:Long =  0L, var username: String = "", var password:String = "",
                var firstname:String = "", var lastname:String = "", var user_image: ByteArray = byteArrayOf()) {
    data class User(    var id: Long, var username: String,
                         var password: String, var firstname: String,
                         var lastname: String, var user_image: ByteArray){
        constructor() : this(0L, "", "", "", "", byteArrayOf())
    }
}