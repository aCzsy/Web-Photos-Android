package com.example.final_project_android_version

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.annotation.Nullable
import androidx.window.layout.WindowMetricsCalculator


class GridViewAdapter(val context: Context, private val imageList: List<RecyclerItem>): BaseAdapter() {
    private val _context: Context = context
    override fun getView(position: Int, p1: View?, parent: ViewGroup?): View {
        val img = ImageView(parent?.context)
        val item: RecyclerItem = getItem(position)

        val decodedImageBytes: ByteArray = Base64.decode(item.file_data, Base64.DEFAULT)
        //convert into bitmap and set view holder
        val bit = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.size)
        img.setImageBitmap(bit)
        img.scaleType = ImageView.ScaleType.FIT_XY

        //getting device's screen dimensions
        val windowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(_context as Activity)
        val currentBounds = windowMetrics.bounds
        val width = currentBounds.width()

        //manually setting grid view items width and height
        //width = (screen width/ 2) - 35
        img.layoutParams = ViewGroup.LayoutParams((width/2)-35,400)

        return img
    }

    override fun getCount(): Int {
        return imageList.size
    }

    override fun getItem(position: Int):RecyclerItem{
        return imageList[position]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }
}