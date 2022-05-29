package com.example.final_project_android_version

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface APIservice {
//    @GET("/android/getUsersImages/{username}")
//    suspend fun getUserImages(@Header("Authorization") token: String?, @Path("username") username: String): Response<List<Image>>

    @GET("/android/getUsersImages/{username}")
    suspend fun getUserImages_convert(@Header("Authorization") token: String?, @Path("username") username: String): Response<List<Image>>

    @POST("/android/upload-image")
    suspend fun upload_image(@Header("Authorization") token: String?,@Body uploadImageRequestAndroid: UploadImageRequestAndroid): Response<ResponseBody>

    @GET("/android/getImage/{imageId}")
    suspend fun getImage(@Header("Authorization") token: String?, @Path("imageId") imageId: Long): Response<Image>

    @POST("/android/delete-image")
    suspend fun delete_image(@Header("Authorization") token: String?, @Body deleteImageRequest: DeleteImageRequest): Response<ResponseBody>

    @POST("/android/update-image")
    suspend fun update_image_details(@Header("Authorization") token: String?, @Body updateImageDetailsRequest: UpdateImageDetailsRequest): Response<ResponseBody>
}