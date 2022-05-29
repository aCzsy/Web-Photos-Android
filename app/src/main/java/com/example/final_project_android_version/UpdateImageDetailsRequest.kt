package com.example.final_project_android_version

import com.google.gson.annotations.SerializedName

data class UpdateImageDetailsRequest (
    @SerializedName("imageId")
    var imageId:Long?,
    @SerializedName("category")
    var category: String?,
    @SerializedName("comment")
    var comment: String?,
    @SerializedName("username")
    var username: String?
    )