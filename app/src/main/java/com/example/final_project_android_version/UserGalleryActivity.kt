package com.example.final_project_android_version

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.window.layout.WindowMetricsCalculator
import com.facebook.shimmer.ShimmerFrameLayout
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates


class UserGalleryActivity : AppCompatActivity(){
    private val mapper = ObjectMapper()
    private lateinit var logout: Button
    private lateinit var user_images:Button
    private lateinit var picker:Button

    private lateinit var fetched_image : ImageView
    private lateinit var recycler_adapter: RecyclerAdapter
    private lateinit var gridview_adapter: GridViewAdapter

    private lateinit var _token:String
    private lateinit var _username:String

    private lateinit var _placeholder_item:View
    private lateinit var _placeholder_item2:View
    private lateinit var placeholder_animation_container:ShimmerFrameLayout

    private var width by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_gallery)

        val windowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
        val currentBounds = windowMetrics.bounds
        width = currentBounds.width()


        var token = intent.getStringExtra("token")
        var username = intent.getStringExtra("username")

        if (token != null) {
            _token = token
        }
        if (username != null) {
            _username = username
        }

        supportActionBar?.title = "Hi, " + _username

        //picker = findViewById(R.id.open_files)
        //fetched_image = findViewById(R.id.fetched_img)
        //logout = findViewById(R.id.logout)
        //user_images = findViewById(R.id.get_user_images_btn)

        Log.wtf("GALLERY USERNAME", username)
        Log.wtf("GALLERY TOKEN=", token)

        if (username != null) {
            getUserImages(token, username)
        }
        _grudview = findViewById(R.id.user_images)

        _grudview.onItemClickListener = OnItemClickListener { adapterView, view, index, id ->
            Log.wtf("ID=", id.toString())

            val item:RecyclerItem = gridview_adapter.getItem(index)


            var intent: Intent = Intent(applicationContext,ImageDisplayActivity::class.java)
            intent.putExtra("token",token)
            intent.putExtra("username",username)
            intent.putExtra("imageId", id)
            intent.putExtra("category", item.category)
            intent.putExtra("comment", item.comment)
            startActivity(intent)
        }

        placeholder_animation_container = findViewById<ShimmerFrameLayout>(R.id.placeholder_animation_container)
        _placeholder_item = findViewById(R.id.placeholder_item)
        _placeholder_item2 = findViewById(R.id.placeholder_item2)

        //resizePlaceholderItems()

        gridview_adapter = GridViewAdapter(this,imageArrayList)
        _grudview.adapter = gridview_adapter

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            _placeholder_item.layoutParams.width = (width/2)-35
            _placeholder_item.layoutParams.height = 600

            _placeholder_item2.layoutParams.width = (width/2)-35
            _placeholder_item2.layoutParams.height = 600

            gridview_adapter.resizeGridItems()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            _placeholder_item.layoutParams.width = (width/2)-35
            _placeholder_item.layoutParams.height = 400

            _placeholder_item2.layoutParams.width = (width/2)-35
            _placeholder_item2.layoutParams.height = 400
        }
    }

//    fun resizePlaceholderItems(){
//        //getting device's screen dimensions
//        val windowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
//        val currentBounds = windowMetrics.bounds
//        val width = currentBounds.width()
//
//        _placeholder_item.layoutParams.width = (width/2)-35
//        _placeholder_item.layoutParams.height = 400
//
//        _placeholder_item2.layoutParams.width = (width/2)-35
//        _placeholder_item2.layoutParams.height = 400
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // determine which menu item was selected
        when(item.itemId) {
            R.id.add -> {
                var intent: Intent = Intent(applicationContext,PickerActivity::class.java)
                intent.putExtra("token",_token)
                intent.putExtra("username",_username)
                startActivity(intent)
            }
            R.id.first -> {
                var intent: Intent = Intent(applicationContext,PickerActivity::class.java)
                intent.putExtra("token",_token)
                intent.putExtra("username",_username)
                startActivity(intent)
            }
            R.id.logout -> {
                val intent: Intent = Intent(applicationContext,LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun getUserImages(token: String?, username: String) {

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
//                            val _img = RecyclerItem(items[i])
//                            Base64.decode(_img.file_data, Base64.NO_WRAP);
//                            imageArrayList.add(_img)
                            /**
                             * Converting image into RecyclerItem.
                             * Encoded string(image data) will be converted into byte array
                             * and then decoded into ByteArray in GridViewAdapter class.
                             */
                            imageArrayList.add(RecyclerItem(items[i]))
                            gridview_adapter.notifyDataSetChanged()
//                            _gridview_arraylist.add(RecyclerItem(items[i]))
//                            recycler_adapter.notifyDataSetChanged()
                            // ID
                            val image_name = items[i].image_name ?: "N/A"
                            //Log.d("Image name: ", image_name)

                            // Employee Name
                            val content_type = items[i].content_type ?: "N/A"
                            //Log.d("Content type: ", content_type)

                            // Employee Salary
                            val category = items[i].category ?: "N/A"
                            //Log.d("Category: ", category)

                            // Employee Age
                            val image_size = items[i].image_size ?: "N/A"
                            //Log.d("Image size: ", image_size)

                            // Employee Age
                            val comment = items[i].comment ?: "N/A"
                            //Log.d("Comment: ", comment)

//                            val file_data = items[i].file_data?: ""
//                            Log.d("File data", file_data.toByteArray().toString())

                            // Employee Age
//                            val image_size = items[i].image_size ?: "N/A"
//                            Log.d("Image size: ", image_size)
                        }
                        placeholder_animation_container.stopShimmerAnimation()
                        placeholder_animation_container.visibility = View.GONE
                    }

                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        placeholder_animation_container.startShimmerAnimation()
    }

    public override fun onPause() {
        placeholder_animation_container.stopShimmerAnimation()
        super.onPause()
    }

    private lateinit var _recyclerview: RecyclerView
    private var _rl_arraylist : ArrayList<RecyclerItem> = ArrayList<RecyclerItem>()
    private var _count: Int = 0


    private lateinit var _grudview: GridView
    private var _gridview_arraylist : ArrayList<RecyclerItem> = ArrayList<RecyclerItem>()
    private var imageArrayList: ArrayList<RecyclerItem> = ArrayList<RecyclerItem>()
}