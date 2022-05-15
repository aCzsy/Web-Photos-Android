package com.example.final_project_android_version

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON.parseArray
import com.alibaba.fastjson.JSONArray
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class UserGalleryActivity : AppCompatActivity() {
    private val mapper = ObjectMapper()
    private lateinit var logout: Button
    private lateinit var user_images:Button
    private lateinit var fetched_image : ImageView
    private lateinit var recycler_adapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_gallery)

        var token = intent.getStringExtra("token")
        var username = intent.getStringExtra("username")

        fetched_image = findViewById(R.id.fetched_img)
        logout = findViewById(R.id.logout)
        user_images = findViewById(R.id.get_user_images_btn)

        Log.wtf("GALLERY USERNAME", username)
        Log.wtf("GALLERY TOKEN=", token)


        _recyclerview = findViewById<RecyclerView>(R.id.users_images)

        // set a linear layout manager on the recycler view then generate an adapter and attach it
//        to the recycler view
        _recyclerview.layoutManager = LinearLayoutManager(this)
        recycler_adapter = RecyclerAdapter(this, _rl_arraylist)
        _recyclerview.adapter = recycler_adapter




        user_images.setOnClickListener( object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if (username != null) {
                    //getMethod(token,username)
                    parseJSONArray(token, username)
                }
            }
        })


//        Thread {
//                val okHttpClient = OkHttpClient()
//                val request = okhttp3.Request.Builder()
//                    .url(WebServiceConnectionSettings.GET_IMAGES + username)
//                    .addHeader("Authorization", token!!)
//                    .build()
//                try {
//                    okHttpClient.newCall(request).execute().use { response ->
//                        //val image: Image? = mapper.readValue(response.body?.string(), Image::class.java)
//
//                        //val res = response.body?.string()
//
//                        val images: List<Image> = JSONArray.parseArray(
//                            response.body?.string(),
//                            Image::class.java
//                        )
////                        Log.wtf("Image name:", image?.image_name)
//                        Looper.prepare()
//                        if (!images.isEmpty()) {
//                            //Toast.makeText(this, "Image fetched, image:" + image?.image_name + " category:" + image?.category, Toast.LENGTH_SHORT).show()
//                            Toast.makeText(this, "Images fetched", Toast.LENGTH_SHORT).show()
//                            Log.wtf("IAMGES", images.toString())
//
////                            val typeToken = object : TypeToken<List<Image>>() {}.type
////                            val authors = Gson().fromJson<List<Image>>(images, typeToken)
//
//                            //Using Main (UI thread) to update the ImageView
////                            val handler = Handler(Looper.getMainLooper())
////                            handler.post {
////                                val bitmapImage = BitmapFactory.decodeByteArray(
////                                    image?.file_data,
////                                    0,
////                                    image?.file_data!!.size
////                                )
////                                fetched_image.setImageBitmap(bitmapImage)
////                            }
//                        } else {
//                            Toast.makeText(this, "Image fetching failed", Toast.LENGTH_SHORT).show()
//                        }
//                        Looper.loop()
//                    }
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }.start()




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

//        val get_image: Button = findViewById(R.id.get_images_btn)
//        get_image.setOnClickListener { v ->
//            Thread {
//                val okHttpClient = OkHttpClient()
//                val request = okhttp3.Request.Builder()
//                    .url(WebServiceConnectionSettings.GET_IMAGE + 5L)
//                    .addHeader("Authorization", token!!)
//                    .build()
//                try {
//                    okHttpClient.newCall(request).execute().use { response ->
//                        val image: Image? = mapper.readValue(response.body?.string(), Image::class.java)
//
////                        val users: List<User> = JSONArray.parseArray(
////                            response.body().string(),
////                            Image::class.java
////                        )
////                        Log.wtf("Image name:", image?.image_name)
//                        Looper.prepare()
//                        if (!(image?.image_name.isNullOrEmpty())) {
//                            Toast.makeText(this, "Image fetched, image:" + image?.image_name + " category:" + image?.category, Toast.LENGTH_SHORT).show()
//                            //Using Main (UI thread) to update the ImageView
//                            val handler = Handler(Looper.getMainLooper())
//                            handler.post {
//                                val bitmapImage = BitmapFactory.decodeByteArray(
//                                    image?.file_data,
//                                    0,
//                                    image?.file_data!!.size
//                                )
//                                fetched_image.setImageBitmap(bitmapImage)
//                            }
//                        } else {
//                            Toast.makeText(this, "Image fetching failed", Toast.LENGTH_SHORT).show()
//                        }
//                        Looper.loop()
//                    }
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }.start()
//        }
    }


    private fun getMethod(token:String?, username:String) {

        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(WebServiceConnectionSettings.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        // Create Service
        val service = retrofit.create(APIservice::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            /*
             * For @Query: You need to replace the following line with val response = service.getEmployees(2)
             * For @Path: You need to replace the following line with val response = service.getEmployee(53)
             */

            // Do the GET request and get response
            val response = service.getUserImages(token,username)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                        )
                    )
                    Log.d("Pretty Printed JSON :", prettyJson)

//                    val intent = Intent(this@MainActivity, DetailsActivity::class.java)
//                    intent.putExtra("json_results", prettyJson)
//                    this@MainActivity.startActivity(intent)

                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }

    fun parseJSONArray(token: String?, username: String) {

        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(WebServiceConnectionSettings.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create Service
        val service = retrofit.create(APIservice::class.java)
        CoroutineScope(Dispatchers.IO).launch {

            // Do the GET request and get response
            val response = service.getUserImages_convert(token, username)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    val items = response.body()
                    if (items != null) {
                        for (i in 0 until items.count()) {
                            _rl_arraylist.add(RecyclerItem(items[i]))
                            recycler_adapter.notifyDataSetChanged()
                            // ID
                            val image_name = items[i].image_name ?: "N/A"
                            Log.d("Image name: ", image_name)

                            // Employee Name
                            val content_type = items[i].content_type ?: "N/A"
                            Log.d("Content type: ", content_type)

                            // Employee Salary
                            val category = items[i].category ?: "N/A"
                            Log.d("Category: ", category)

                            // Employee Age
                            val image_size = items[i].image_size ?: "N/A"
                            Log.d("Image size: ", image_size)

                            // Employee Age
                            val comment = items[i].comment ?: "N/A"
                            Log.d("Comment: ", comment)

//                            val file_data = items[i].file_data?: ""
//                            Log.d("File data", file_data.toByteArray().toString())

                            // Employee Age
//                            val image_size = items[i].image_size ?: "N/A"
//                            Log.d("Image size: ", image_size)
                        }
                    }

                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }

    private lateinit var _recyclerview: RecyclerView
    private var _rl_arraylist : ArrayList<RecyclerItem> = ArrayList<RecyclerItem>()
    private var _count: Int = 0

}