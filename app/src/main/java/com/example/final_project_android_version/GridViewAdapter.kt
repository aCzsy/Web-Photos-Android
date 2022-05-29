package com.example.final_project_android_version

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.window.layout.WindowMetricsCalculator


class GridViewAdapter(val context: Context, private val imageList: List<RecyclerItem>): BaseAdapter(){
    private val _context: Context = context
    private var mViewClickListener: ViewClickListener? = null
    private lateinit var _item:RecyclerItem

    interface ViewClickListener {
        fun onImageClicked(position: Int)
    }

    fun setViewClickListener(viewClickListener: ViewClickListener?) {
        mViewClickListener = viewClickListener
    }

    override fun getView(position: Int, p1: View?, parent: ViewGroup?): View {
        val img = ImageView(parent?.context)
        val item: RecyclerItem = getItem(position)
        _item = item

        /**
         * Decoding String into byte array
         */
        val decodedImageBytes: ByteArray = Base64.decode(item.file_data, Base64.DEFAULT)
        /**
         * Converting into bitmap and setting view holder
         */
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


    /**
     * @param image index inside Grid View arraylist
     * @return image ID
     */
    override fun getItemId(p0: Int): Long {
        return imageList[p0].imageId!!
    }


}