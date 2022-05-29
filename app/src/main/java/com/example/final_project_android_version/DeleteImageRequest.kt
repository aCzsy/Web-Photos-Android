package com.example.final_project_android_version

import com.google.gson.annotations.SerializedName

data class DeleteImageRequest (
    @SerializedName("imageId")
    var imageId:Long?,
    @SerializedName("username")
    var username: String?,
)