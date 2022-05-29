package com.example.final_project_android_version

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import kotlin.math.max
import kotlin.math.min

class ImageDisplayActivity : AppCompatActivity() {
    private lateinit var _image_displayed: ImageView
//    private lateinit var scaleGestureDetector: ScaleGestureDetector
    // on below line we are defining our scale factor.
//    var mScaleFactor:Float = 1.0f

    private lateinit var delete_btn:Button
    private lateinit var image_display_layout:ConstraintLayout
    private lateinit var req_loading:ConstraintLayout
    private lateinit var edit_note_container_layout:FrameLayout
    private lateinit var edit_image_note:EditText
    private lateinit var _category_select:Spinner
    private lateinit var edit_note_btn:Button
    private lateinit var edit_image_note_btn:Button
    private lateinit var loader_message:TextView
    private lateinit var galleryIntent: Intent
    private lateinit var _username:String
    private lateinit var img_category:String
    private lateinit var cancel_img_update_btn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)


        var token = intent.getStringExtra("token")
        var username = intent.getStringExtra("username")
        var imageId = intent.getLongExtra("imageId", 0L)
        var image_category = intent.getStringExtra("category")
        var image_note = intent.getStringExtra("comment")

        _username = username!!

        supportActionBar?.title = ""

        val actionBar = supportActionBar

        //enabling back button in action bar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        _image_displayed = findViewById(R.id.image_displayed)
        // on below line we are initializing our scale gesture detector for zoom in and out for our image.
        // on below line we are initializing our scale gesture detector for zoom in and out for our image.
//        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        getImage(token, imageId)

        delete_btn = findViewById(R.id.delete_image)
        image_display_layout = findViewById(R.id.image_display_layout)
        req_loading = findViewById(R.id.req_loading)
        edit_note_container_layout = findViewById(R.id.edit_note_container_layout)
        edit_image_note = findViewById<EditText>(R.id.edit_image_note)
        _category_select = findViewById(R.id.edit_category_select)
        edit_note_btn = findViewById(R.id.edit_note_btn)
        edit_image_note_btn = findViewById(R.id.edit_image_note_btn)
        loader_message = findViewById(R.id.loader_message)
        cancel_img_update_btn = findViewById(R.id.cancel_img_update)

        galleryIntent = Intent(applicationContext,UserGalleryActivity::class.java)

        Log.wtf("NOTE:", image_note)
        //Setting image note EditText field to current image's note which can be edited
        edit_image_note.setText(image_note)

        var adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.category_options,
            android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        _category_select.setAdapter(adapter)

        val spinner_def_value = adapter.getPosition(adapter.getItem(0))
        _category_select.setSelection(spinner_def_value)

        img_category = _category_select.selectedItem.toString()


        _category_select.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                img_category = p0?.getItemAtPosition(p2).toString()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })

        cancel_img_update_btn.setOnClickListener( object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var intent: Intent = Intent(applicationContext,UserGalleryActivity::class.java)
                intent.putExtra("token",token)
                intent.putExtra("username",username)
                startActivity(intent)
            }
        })

        edit_image_note_btn.setOnClickListener( object : View.OnClickListener {
            override fun onClick(p0: View?) {
                edit_note_container_layout.bringToFront()
                edit_note_container_layout.visibility = View.VISIBLE;
                delete_btn.isEnabled = false
                edit_image_note_btn.isEnabled = false
            }
        })

        edit_note_btn.setOnClickListener( object : View.OnClickListener {
            override fun onClick(p0: View?) {
                if(edit_image_note.text.toString() != ""){
                    // Prepare the View for the animation
                    edit_note_container_layout.visibility = View.GONE;
                    try {
                        Log.wtf("IMAGEID=",imageId.toString())
                        Log.wtf("CAT=",image_category)
                        Log.wtf("NOTE=", edit_image_note.text.toString())
                        updateImageInfo(token,imageId,img_category, edit_image_note.text.toString(), username)
                    } catch (e:Exception){
                        e.printStackTrace();
                    }
                } else{
                    val snackbar = Snackbar.make(image_display_layout, "Please add note", Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
            }
        })

        delete_btn.setOnClickListener( object : View.OnClickListener {
            override fun onClick(p0: View?) {
                delete_btn.isEnabled = false

                try {
                    deleteImage(token,imageId, username)
                } catch (e:Exception){
                    e.printStackTrace();
                }
            }
        })
    }

    private fun updateImageInfo(token: String?, imageId:Long, category: String, comment:String, username:String){
        val updateImageInfoReq = UpdateImageDetailsRequest(imageId, category, comment, username)

        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(WebServiceConnectionSettings.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create Service
        val service = retrofit.create(APIservice::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            runOnUiThread {
                // Stuff that updates the UI
                loader_message.text = "Updating image details..."
                req_loading.bringToFront()
                req_loading.visibility = View.VISIBLE
            }
            // Do the POST request and get response
            val response = service.update_image_details(token, updateImageInfoReq)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body().toString()
                        )
                    )
                    val snackbar = Snackbar.make(image_display_layout, "Details updated successfully", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    Log.d("Pretty Printed JSON :", prettyJson)
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        galleryIntent.putExtra("token", token)
                        galleryIntent.putExtra("username", _username)
                        startActivity(galleryIntent)
                    }, 1000)

                } else {
                    val snackbar = Snackbar.make(image_display_layout, "Update request failed", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    Log.e("RETROFIT_ERROR", response.code().toString())
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        galleryIntent.putExtra("token", token)
                        galleryIntent.putExtra("username", _username)
                        startActivity(galleryIntent)
                    }, 1000)
                }
            }
        }
    }

    private fun deleteImage(token: String?, imageId:Long, username:String){
        val deleteReq = DeleteImageRequest(imageId, username)

        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(WebServiceConnectionSettings.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create Service
        val service = retrofit.create(APIservice::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            runOnUiThread {
                // Stuff that updates the UI
                req_loading.bringToFront()
                req_loading.visibility = View.VISIBLE
            }
            // Do the POST request and get response
            val response = service.delete_image(token, deleteReq)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body().toString()
                        )
                    )
                    val snackbar = Snackbar.make(image_display_layout, "Image deleted successfully", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    Log.d("Pretty Printed JSON :", prettyJson)
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        galleryIntent.putExtra("token", token)
                        galleryIntent.putExtra("username", _username)
                        startActivity(galleryIntent)
                    }, 1000)

                } else {
                    val snackbar = Snackbar.make(image_display_layout, "Delete request failed", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    Log.e("RETROFIT_ERROR", response.code().toString())
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        galleryIntent.putExtra("token", token)
                        galleryIntent.putExtra("username", _username)
                        startActivity(galleryIntent)
                    }, 1000)
                }
            }
        }
    }

    private fun getImage(token:String?, id:Long) {
        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(WebServiceConnectionSettings.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create Service
        val service = retrofit.create(APIservice::class.java)

        CoroutineScope(Dispatchers.IO).launch {

            // Do the GET request and get response
            val response = service.getImage(token,id)
            val receivedImage = response.body()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val _img = RecyclerItem(receivedImage!!)
                    val img = Base64.decode(_img.file_data, Base64.DEFAULT);
                    val bitmapImage = BitmapFactory.decodeByteArray(
                                    img,
                                    0,
                                    img.size
                                )
                    _image_displayed.setImageBitmap(bitmapImage)
                } else {
                    Log.e("RETROFIT_ERROR", response.code().toString())
                }
            }
        }
    }

    //Return to previous activity when back button is pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_in_picker, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // determine which menu item was selected
        when(item.itemId) {
            R.id.logout -> {
                val intent: Intent = Intent(applicationContext,LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
//        // inside on touch event method we are calling on
//        // touch event method and pasing our motion event to it.
//        scaleGestureDetector.onTouchEvent(motionEvent)
//        return true
//    }
//
//    inner class ScaleListener() : SimpleOnScaleGestureListener() {
//        // on below line we are creating a class for our scale
//        // listener and  extending it with gesture listener.
//        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
//            // inside on scale method we are setting scale
//            // for our image in our image view.
//            mScaleFactor *= scaleGestureDetector.scaleFactor
//            mScaleFactor = max(0.1f, min(mScaleFactor, 10.0f))
//
//            // on below line we are setting
//            // scale x and scale y to our image view.
//            _image_displayed.scaleX = mScaleFactor
//            _image_displayed.scaleY = mScaleFactor
//            return true
//        }
//    }
}