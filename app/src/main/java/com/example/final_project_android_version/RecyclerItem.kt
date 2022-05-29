package com.example.final_project_android_version

class RecyclerItem constructor(image: Image) {
    // private fields of the class
    public var imageId = image.imageId
    private var image_name = image.image_name
    private var content_type = image.content_type
    public var category = image.category
    private var image_size = image.image_size
    public var comment = image.comment
    public var file_data = image.file_data?.toByteArray()
}


