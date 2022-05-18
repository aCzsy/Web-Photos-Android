package com.example.final_project_android_version

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface APIservice {
    @GET("/android/getUsersImages/{username}")
    suspend fun getUserImages(@Header("Authorization") token: String?, @Path("username") username: String): Response<ResponseBody>

    @GET("/android/getUsersImages/{username}")
    suspend fun getUserImages_convert(@Header("Authorization") token: String?, @Path("username") username: String): Response<List<Image>>

    @POST("/android/upload-image")
    suspend fun upload_image(@Header("Authorization") token: String?,@Body uploadImageRequestAndroid: UploadImageRequestAndroid): Response<ResponseBody>
}