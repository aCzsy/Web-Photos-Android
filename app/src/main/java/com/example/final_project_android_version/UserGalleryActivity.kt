package com.example.final_project_android_version

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import java.io.IOException

class UserGalleryActivity : AppCompatActivity() {
    private val mapper = ObjectMapper()
    private lateinit var logout: Button
    private lateinit var fetched_image : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_gallery)

        var token = intent.getStringExtra("token")

        fetched_image = findViewById(R.id.fetched_img)
        logout = findViewById(R.id.logout)

        logout.setOnClickListener { v: View? ->
            Thread {
                val okHttpClient = OkHttpClient()

                val request = okhttp3.Request.Builder()
                    .url(WebServiceConnectionSettings.LOGOUT)
                    .build()
                try {
                    okHttpClient.newCall(request).execute().use { response ->
                        Looper.prepare()
                        if (response.isSuccessful) {

                            Toast.makeText(this, "Logout success", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Logout failed", Toast.LENGTH_SHORT).show()
                        }
                        Looper.loop()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }

        val get_image: Button = findViewById(R.id.get_images_btn)
        get_image.setOnClickListener { v ->
            Thread {
                val okHttpClient = OkHttpClient()
                val request = okhttp3.Request.Builder()
                    .url(WebServiceConnectionSettings.GET_IMAGE + 5L)
                    .addHeader("Authorization", token!!)
                    .build()
                try {
                    okHttpClient.newCall(request).execute().use { response ->
                        val image: Image? = mapper.readValue(response.body?.string(), Image::class.java)

//                        val users: List<User> = JSONArray.parseArray(
//                            response.body().string(),
//                            Image::class.java
//                        )
//                        Log.wtf("Image name:", image?.image_name)
                        Looper.prepare()
                        if (!(image?.image_name.isNullOrEmpty())) {
                            Toast.makeText(this, "Image fetched, image:" + image?.image_name + " category:" + image?.category, Toast.LENGTH_SHORT).show()
                            //Using Main (UI thread) to update the ImageView
                            val handler = Handler(Looper.getMainLooper())
                            handler.post {
                                val bitmapImage = BitmapFactory.decodeByteArray(
                                    image?.file_data,
                                    0,
                                    image?.file_data!!.size
                                )
                                fetched_image.setImageBitmap(bitmapImage)
                            }
                        } else {
                            Toast.makeText(this, "Image fetching failed", Toast.LENGTH_SHORT).show()
                        }
                        Looper.loop()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }
}