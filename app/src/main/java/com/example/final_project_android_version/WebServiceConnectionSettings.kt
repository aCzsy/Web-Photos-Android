package com.example.final_project_android_version

class WebServiceConnectionSettings {
    companion object{
        private const val CLOUD_HOST = "final-software-project.herokuapp.com"
        private const val PROTOCOL = "https"
        const val URL = "$PROTOCOL://$CLOUD_HOST"
        const val LOG_IN = "$PROTOCOL://$CLOUD_HOST/android/authenticate"
        const val UPLOAD_IMAGE = "$PROTOCOL://$CLOUD_HOST/android/upload_image/"

        //private const val HOST = "127.0.0.1"
        //private const val LOCAL_MACHINE_HOST = "192.168.1.73"
        //private const val PORT = "8080"
        //private const val PROTOCOL = "http"
        //const val URL = "$PROTOCOL://$LOCAL_MACHINE_HOST:$PORT"
//        const val URL = "$PROTOCOL://$LOCAL_MACHINE_HOST"
//        const val LOG_IN = "$PROTOCOL://$LOCAL_MACHINE_HOST:$PORT/android/authenticate"
//        const val LOGOUT = "$PROTOCOL://$LOCAL_MACHINE_HOST:$PORT/android/logout"
//        const val GET_IMAGE = "$PROTOCOL://$LOCAL_MACHINE_HOST:$PORT/android/getImage/"
//        const val GET_IMAGES = "$PROTOCOL://$LOCAL_MACHINE_HOST:$PORT/android/getUsersImages/"
//        const val UPLOAD_IMAGE = "$PROTOCOL://$LOCAL_MACHINE_HOST:$PORT/android/upload_image/"
    }
}