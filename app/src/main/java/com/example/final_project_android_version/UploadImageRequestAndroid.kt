package com.example.final_project_android_version

import com.google.gson.annotations.SerializedName

data class UploadImageRequestAndroid (
    @SerializedName("username")
    var username: String?,
    @SerializedName("imageDTO_android")
    var imageDTO_android: ImageDTO_Android
)