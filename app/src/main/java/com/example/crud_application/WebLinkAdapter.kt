package com.example.crud_application

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ScrollCaptureCallback
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class WebLinkAdapter : RecyclerView.Adapter<WebLinkAdapter.WebLinkViewHolder>() {
    private var list = mutableListOf<WebLink>()
    private var onClickView:((WebLink)->Unit)?=null
    private var onLongClickView:((WebLink)->Unit)?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebLinkViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.weblink_item_recyclerview, parent, false)
        return WebLinkViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WebLinkViewHolder, position: Int) {
        val webLink = list[position]
        holder.setItem(webLink)
        holder.setOnClickView {
            onClickView?.invoke(it)
        }
        holder.setOnLongClickView {
            onLongClickView?.invoke(it)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setItems(list: MutableList<WebLink>){
        this.list = list
        notifyDataSetChanged()
    }

    fun setOnClickView(callback: (WebLink) -> Unit){
        this.onClickView = callback
    }

    fun setOnLongClickView(callback: (WebLink) -> Unit){
        this.onLongClickView = callback
    }


//    fun deleteItem(position: Int) {
//        // Get the item to be deleted
//        val itemToDelete = list[position]
//
//        // Remove the item from the local data source
//        list.removeAt(position)
//        notifyItemRemoved(position)
//
//        // Remove the item from Firebase Realtime Database
//        firebaseDatabase = FirebaseDatabase.getInstance()
//        databaseReference = firebaseDatabase?.getReference("data")
//        databaseReference?.child(itemToDelete.key)?.removeValue()
//    }


    class WebLinkViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        private var txtWebLink: TextView? = null
        private var onClickView:((WebLink)->Unit)?=null
        private var onLongClickView:((WebLink)->Unit)?=null

        fun setItem(data: WebLink){
            txtWebLink = itemView.findViewById(R.id.txtWebLink)
            txtWebLink?.text = data.webLink

            itemView.setOnLongClickListener {
                onLongClickView?.invoke(data)
                val position = adapterPosition
                true
            }

            itemView.setOnClickListener {
                onClickView?.invoke(data)
                val position = adapterPosition
            }

        }

        fun setOnClickView(callback: (WebLink) -> Unit){
            this.onClickView = callback
        }

        fun setOnLongClickView(callback: (WebLink) -> Unit){
            this.onLongClickView = callback
        }

    }

}