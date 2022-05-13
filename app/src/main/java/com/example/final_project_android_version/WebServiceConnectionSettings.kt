package com.example.final_project_android_version

class WebServiceConnectionSettings {
    companion object{
        private const val HOST = "127.0.0.1"
        private const val LOCAL_MACHINE_HOST = "192.168.1.123"
        private const val PORT = "8080"
        private const val PROTOCOL = "http"
        const val LOG_IN = "$PROTOCOL://$LOCAL_MACHINE_HOST:$PORT/android/authenticate"
        const val LOGOUT = "$PROTOCOL://$LOCAL_MACHINE_HOST:$PORT/android/logout"
        const val GET_IMAGE = "$PROTOCOL://$LOCAL_MACHINE_HOST:$PORT/android/getImage/"
    }
}