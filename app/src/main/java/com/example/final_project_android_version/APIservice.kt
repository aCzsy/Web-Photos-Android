package com.example.final_project_android_version

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface APIservice {
    @GET("/android/getUsersImages/{username}")
    suspend fun getUserImages(@Header("Authorization") token: String?, @Path("username") username: String): Response<ResponseBody>

    @GET("/android/getUsersImages/{username}")
    suspend fun getUserImages_convert(@Header("Authorization") token: String?, @Path("username") username: String): Response<List<Image>>
}