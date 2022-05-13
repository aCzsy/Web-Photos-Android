package com.example.final_project_android_version

//import com.android.volley.RequestQueue
//import com.android.volley.toolbox.StringRequest
//import com.android.volley.toolbox.Volley

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
//import com.android.volley.Response
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var startAppBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startAppBtn = findViewById(R.id.startAppButton)

        startAppBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                startLoginActivity()
            }

        })
    }

    private fun startLoginActivity(){
        var intent: Intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }
}