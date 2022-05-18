package com.example.final_project_android_version

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private val mapper = ObjectMapper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //hiding action at the top of the screen
        supportActionBar?.hide()

        var fullToken:FullJwtToken = FullJwtToken("")

//        //Make sure we have permission to access phone's storage
//        ActivityCompat.requestPermissions(
//            this, arrayOf<String>(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ), PackageManager.PERMISSION_GRANTED
//        )

        val login = findViewById<Button>(R.id.auth_btn)

        login.setOnClickListener {
            Thread {
                val username =
                    (findViewById<View>(R.id.username) as EditText).text
                        .toString()
                val password =
                    (findViewById<View>(R.id.pw) as EditText).text.toString()

                val jsonObject = JSONObject()
                try {
                    jsonObject.put("username", username)
                    jsonObject.put("password", password)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                val client = OkHttpClient()
                val JSON = "application/json; charset=utf-8".toMediaType()
                val body = jsonObject.toString().toRequestBody(JSON)
                Log.wtf("BODY=",body.toString())
                val request = Request.Builder()
                    .url(WebServiceConnectionSettings.LOG_IN)
                    //.addHeader("Authorization","Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhIiwiZXhwIjoxNjUxNDUwMzgzLCJpYXQiOjE2NTE0MzIzODN9.XylmQFWqGWfVjqtQwitJ5Ybgdp02F81U8wwolB6o-9bZVdVSmXat4SR-a59o79ag51wqdJZ3dyUBFLRVs4bE9w")
                    .post(body)
                    .build()

                var response: Response? = null
                try {
                    response = client.newCall(request).execute()
                    //Mapping response to corresponding class with field token
                    val jwtResponse: JwtTokenResponse? = mapper.readValue(response.body?.string(), JwtTokenResponse::class.java)
                    //val resStr = response.body!!.string()
                    Log.wtf("JWT=",jwtResponse?.token)

                    //Creating full token which can be used for all api calls within application
                    //var fullToken:FullJwtToken = FullJwtToken(jwtResponse?.token)
                    fullToken = FullJwtToken(jwtResponse?.token)
                    Log.wtf("Full Jwt=",fullToken.fullToken)
                    Log.wtf("USERNAME=", jwtResponse?.username)

                    var intent: Intent = Intent(applicationContext,UserGalleryActivity::class.java)
                    intent.putExtra("token",fullToken.fullToken)
                    intent.putExtra("username", jwtResponse?.username)

                    //If request was successful (Status code == 200)
                    if(response.code == 200){
                        startActivity(intent)
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }
}