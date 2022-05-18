package com.example.final_project_android_version

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.zip.Deflater


class PickerActivity : AppCompatActivity() {
    private lateinit var camera_btn: Button
    private lateinit var gallery_btn: Button
    private lateinit var picker_image: ImageView
    private lateinit var upload_img: Button
    private lateinit var imageToUploadBitmap: Bitmap
    private lateinit var imageToUpload: Image
    private lateinit var file: File
    private lateinit var requestFile:RequestBody
    private lateinit var multipartBody: MultipartBody.Part
    private lateinit var mime_type:String
    private lateinit var encodedImage:ByteArray
    private lateinit var img_upload_layout:RelativeLayout
    private lateinit var galleryIntent: Intent
    private lateinit var logout: Button
    private lateinit var frameLayout: FrameLayout
    private lateinit var add_comment:Button
    private lateinit var image_note:EditText
    private lateinit var _username:String
    private lateinit var _loading_layout_elements:ConstraintLayout
    private lateinit var _category_select:Spinner
    private lateinit var img_category:String
    private lateinit var _cancel_btn:Button

//    private val getResult =
//        registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) {
//            if (it.resultCode == Activity.RESULT_OK) {
//                val value = it.data?.getStringExtra("input")
//                val bitmap: Bitmap = it.data?.extras?.get("data") as Bitmap
//                picker_image.setImageBitmap(bitmap)
//                if(upload_img.visibility != View.VISIBLE){
//                    upload_img.visibility = View.VISIBLE
//                }
//            }
//        }

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    picker_image.setImageURI(uri)
                    val imageUri: Uri = uri
                    mime_type = contentResolver.getType(imageUri).toString()
                    Log.wtf("MIME=", mime_type)
                    file = File(imageUri.path!!)
                    requestFile =
                        file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)
//                    Log.wtf("PATH=",imageUri.path)
//                    Log.wtf("MULTIPART=", multipartBody.toString())
//                    Log.wtf("FILENAME=",file.name)
//                    Log.wtf("SIZE=", file.length().toString())
//                    Log.wtf("content=",multipartBody.body.contentType().toString())
//                    Log.wtf("SIZE MULTIPART=", multipartBody.body.contentLength().toString())
                    if(upload_img.visibility != View.VISIBLE){
                        upload_img.visibility = View.VISIBLE
                        //contentResolver.query(imageUri, null, null, null, null)
                        if(Build.VERSION.SDK_INT >= 29){
                            val source = ImageDecoder.createSource(this.contentResolver, imageUri)
                            imageToUploadBitmap = ImageDecoder.decodeBitmap(source)
                            encodedImage = encodeBitmap(imageToUploadBitmap)
//                            val uriPathHelper = URIPathHelper()
//                            val filePath = uriPathHelper.getPath(this, imageUri)
//                            //Log.i(“FilePath”, filePath.toString())
//                            val file = File(filePath)
//                            val requestFile: RequestBody =
//                                RequestBody.create(MediaType.parse(“multipart/form-data”), file)
//                            val multiPartBody = MultipartBody.Part.createFormData(“file”, file.name, requestFile)

                            Log.wtf("BITMAP",imageToUploadBitmap.toString())
                            //picker_image.setImageBitmap(bitMapDecoded)
                        } else {
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                imageUri
                            )
                            //picker_image.setImageBitmap(bitmap)
                        }
                    }
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                picker_image.setImageURI(uri)
                val imageUri: Uri = uri
                mime_type = contentResolver.getType(imageUri).toString()
                Log.wtf("MIME=", mime_type)
                file = File(imageUri.path!!)
                requestFile =
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)
                Log.wtf("PATH=",imageUri.path)
                if(upload_img.visibility != View.VISIBLE){
                    upload_img.visibility = View.VISIBLE
                    if(Build.VERSION.SDK_INT >= 29){
                        val source = ImageDecoder.createSource(this.contentResolver, imageUri)
                        imageToUploadBitmap = ImageDecoder.decodeBitmap(source)
                        encodedImage = encodeBitmap(imageToUploadBitmap)
                    } else {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            imageUri
                        )
                        //picker_image.setImageBitmap(bitmap)
                    }
                }
            }
        }

    private var latestTmpUri: Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)
        val compressor = Deflater(Deflater.BEST_COMPRESSION)

        supportActionBar?.title = "USER"


        val token = intent.getStringExtra("token")
        val username = intent.getStringExtra("username")

        _username = username!!

        //Make sure we have permission to access phone's storage
        ActivityCompat.requestPermissions(
            this, arrayOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), PackageManager.PERMISSION_GRANTED
        )

        camera_btn = findViewById(R.id.camera_btn)
        gallery_btn = findViewById(R.id.gallery_btn)
        picker_image = findViewById(R.id.picker_image)
        upload_img = findViewById(R.id.upload_img)
        add_comment = findViewById(R.id.add_comment)
        image_note = (findViewById<View>(R.id.image_note) as EditText)
        _category_select = findViewById(R.id.category_select)
        _cancel_btn = findViewById(R.id.cancel_img_upload)

        img_upload_layout = findViewById(R.id.image_upload_layout)
        frameLayout = findViewById(R.id.add_note_container_layout)
        _loading_layout_elements = findViewById(R.id.loading_layout_elements)

        galleryIntent = Intent(applicationContext,UserGalleryActivity::class.java)


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

        _cancel_btn.setOnClickListener( object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var intent: Intent = Intent(applicationContext,PickerActivity::class.java)
                intent.putExtra("token",token)
                intent.putExtra("username",username)
                startActivity(intent)
            }
        })

        upload_img.setOnClickListener( object : View.OnClickListener {
            override fun onClick(p0: View?) {

//                val imageData = encodedImage

                if(image_note.text.toString() != ""){
                    val note = image_note.text.toString()

//                 Compress the data
                    val compressedData = compress(
                        encodedImage,
                        Deflater.BEST_COMPRESSION,
                        false
                    )

                    if(encodedImage.isNotEmpty()){
                        Log.wtf("USERNAME PICKER=", username)
                        val image:ImageDTO_Android = ImageDTO_Android(file.name, mime_type,img_category,encodedImage.size.toString(),note,
                            compressedData!!)
                        val uploadReq = UploadImageRequestAndroid(username, image)
                        try {
                            upload_img(token,uploadReq)
                        } catch (e:Exception){
                            e.printStackTrace();
                        }
                    }
                } else{
                    camera_btn.isEnabled = false
                    gallery_btn.isEnabled = false
                    upload_img.isEnabled = false

                    frameLayout.bringToFront()
                    frameLayout.visibility = View.VISIBLE;
                }
            }
        })

        add_comment.setOnClickListener( object : View.OnClickListener {
            override fun onClick(p0: View?) {

                if(image_note.text.toString() != ""){
                    // Prepare the View for the animation
                    frameLayout.visibility = View.GONE;

                    camera_btn.isEnabled = true
                    gallery_btn.isEnabled = true
                    upload_img.isEnabled = true
                } else{
                    val snackbar = Snackbar.make(img_upload_layout, "Please add note", Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
            }
        })

        camera_btn.setOnClickListener( object : View.OnClickListener {
            override fun onClick(p0: View?) {
                takeImage()
//                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                intent.type = "image/*"
//                getResult.launch(intent)
            }
        })

        gallery_btn.setOnClickListener( object : View.OnClickListener {
            override fun onClick(p0: View?) {
                selectImageFromGallery()
            }
        })

//        logout.setOnClickListener( object : View.OnClickListener {
//            override fun onClick(p0: View?) {
//                val intent: Intent = Intent(applicationContext,LoginActivity::class.java)
//                startActivity(intent)
//            }
//        })

//        val startActivityForResultGallery =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//                if (result.resultCode == Activity.RESULT_OK) {
//                    picker_image.setImageURI(result.data!!.data)
//                }
//            }
//
//        val startActivityForResultCamera =
//            registerForActivityResult(ActivityResultContracts.TakePicture()) { bitmapImage ->
//                picker_image.setImageBitmap(bitmapImage)
//            }
    }

    private fun encodeBitmap(bitmap:Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes: ByteArray = baos.toByteArray()
        //val encodedImage: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        val encodedImage: ByteArray = Base64.encode(imageBytes, Base64.DEFAULT)

        return imageBytes
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
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

    private fun upload_img(token: String?, req: UploadImageRequestAndroid){
        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(WebServiceConnectionSettings.UPLOAD_IMAGE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create Service
        val service = retrofit.create(APIservice::class.java)
        // Create JSON using JSONObject
//        val jsonObject = JSONObject()
//        jsonObject.put("imageDTOAndroid", image)
//
//        // Convert JSONObject to String
//        val jsonObjectString = jsonObject.toString()
//
//        // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
//        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        //val call = service.upload_image(token, image);

//        val response: Response<Response<ResponseBody>> = call.execute()
//
//        if (!response.isSuccessful()) {
//            throw IOException(
//                if (response.errorBody() != null) response.errorBody()!!
//                    .string() else "Unknown error"
//            )
//        }

//
        CoroutineScope(Dispatchers.IO).launch {
            runOnUiThread {
                // Stuff that updates the UI
                _loading_layout_elements.bringToFront()
                _loading_layout_elements.visibility = View.VISIBLE
            }

            // Do the POST request and get response
            val response = service.upload_image(token, req)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body().toString()
                        )
                    )
                    val snackbar = Snackbar.make(img_upload_layout, "Image uploaded successfully", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    Log.d("Pretty Printed JSON :", prettyJson)
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        galleryIntent.putExtra("token", token)
                        galleryIntent.putExtra("username", _username)
                        startActivity(galleryIntent)
                    }, 1000)

                } else {
                    val snackbar = Snackbar.make(img_upload_layout, "Image upload failed", Snackbar.LENGTH_LONG)
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

    @Throws(IOException::class)
    fun compress(
        input: ByteArray?, compressionLevel: Int,
        GZIPFormat: Boolean
    ): ByteArray? {
        // Create a Deflater object to compress data
        val compressor = Deflater(compressionLevel, GZIPFormat)

        // Set the input for the compressor
        compressor.setInput(input)

        // Call the finish() method to indicate that we have
        // no more input for the compressor object
        compressor.finish()

        // Compress the data
        val bao = ByteArrayOutputStream()
        val readBuffer = ByteArray(1024)
        while (!compressor.finished()) {
            val readCount = compressor.deflate(readBuffer)
            if (readCount > 0) {
                // Write compressed data to the output stream
                bao.write(readBuffer, 0, readCount)
            }
        }

        // End the compressor
        compressor.end()

        // Return the written bytes from output stream
        return bao.toByteArray()
    }
}