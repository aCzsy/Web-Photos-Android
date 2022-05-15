package com.example.final_project_android_version

class RecyclerItem constructor(image: Image) {
    // private fields of the class
    private var image_name = image.image_name
    private var content_type = image.content_type
    private var category = image.category
    private var image_size = image.image_size
    private var comment = image.comment
    public var file_data = image.file_data?.toByteArray()


}


