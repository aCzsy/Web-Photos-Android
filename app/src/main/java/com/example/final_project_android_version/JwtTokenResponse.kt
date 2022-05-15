package com.example.final_project_android_version

class JwtTokenResponse(var token:String = "", var username:String = "") {
    class JwtTokenResponse(var token: String, var username: String){
        constructor() : this("", "")
    }
}