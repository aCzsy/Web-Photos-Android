package com.example.final_project_android_version


data class Image(
    var image_name: String = "", var content_type: String = "",
    var category: String = "", var image_size: String = "",
    var comment: String = "", var file_data: ByteArray = byteArrayOf()) {

    data class Image(    var image_name: String, var content_type: String,
                         var category: String, var image_size: String,
                         var comment: String, var file_data: ByteArray){
        constructor() : this("", "", "", "", "", byteArrayOf())
    }

}
