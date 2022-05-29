package com.example.final_project_android_version

import com.google.gson.annotations.SerializedName

//data class ImageDTO_Android(
//    var image_name: String = "", var content_type: String = "",
//    var category: String = "", var image_size: String = "",
//    var comment: String = "", var file_data: ByteArray = byteArrayOf()) {
//
//    data class Image(    var image_name: String, var content_type: String,
//                         var category: String, var image_size: String,
//                         var comment: String, var file_data: ByteArray){
//        constructor() : this("", "", "", "", "", byteArrayOf())
//    }
//
//
//
//}

data class ImageDTO_Android(
    @SerializedName("image_name")
    var image_name: String?,

    @SerializedName("content_type")
    var content_type: String?,

    @SerializedName("category")
    var category: String?,

    @SerializedName("image_size")
    var image_size: String?,

    @SerializedName("comment")
    var comment: String?,

    @SerializedName("file_data")
    var file_data: String
)