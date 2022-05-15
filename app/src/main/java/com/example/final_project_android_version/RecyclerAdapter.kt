package com.example.final_project_android_version

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView


class RecyclerAdapter(val context: Context, var ri_arraylist: ArrayList<RecyclerItem>) :
    RecyclerView.Adapter<RecyclerAdapter.ItemHolder>() {
    // private fields of the class
    private val _context: Context = context
    private var _ri_arraylist: ArrayList<RecyclerItem> = ri_arraylist
    // function that will create a viewholder for the recycler adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
//        val view: View = LayoutInflater.from(parent.context)
//            .inflate(R.layout.card_layout, parent, false)
//        return ItemHolder(view)

        // get access to the layout inflator and inflate a layout for one of our recycler view items
        return ItemHolder(LayoutInflater.from(_context).inflate(R.layout.recycler_image, parent,
            false))
    }

    // function that will bind an item in our arraylist to a view holder so it can be displayed
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        // get the item at the current position
        val item: RecyclerItem = _ri_arraylist.get(position)

        // decode into bytes
        //val decodedImageBytes: ByteArray = Base64.decode(item.file_data, Base64.DEFAULT)
        //convert into bitmap and set view holder
        //val bit = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.size)
        //holder.image_item.setImageBitmap(bit)
    }

    // returns the number of items that are in this RecyclerAdapter
    override fun getItemCount(): Int {
        return _ri_arraylist.size
    }
    // nested class that will implement a view holder for an item in the list
    class ItemHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        // private fields of the class
        private var _view: View = v
        private var _recycler_item: RecyclerItem? = null
        lateinit var image_item: ImageView
        // called to initialise the object
        init {
            // pull references from the layout for the text views
            image_item = _view.findViewById<ImageView>(R.id.image)
            // set a listener for clicks on this view holder
            _view.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            Log.i("RecyclerView", "clicked on image")
        }
    }
}
