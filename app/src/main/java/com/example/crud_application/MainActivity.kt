package com.example.crud_application

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    private var list = mutableListOf<WebLink>()
    private lateinit var txtWebLink: EditText
    private lateinit var btnSave: Button
    private lateinit var recyclerView: RecyclerView


    private var adapter: WebLinkAdapter? = null
    private var selectedID:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)

        initRecyclerView()

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase?.getReference("data")

        txtWebLink = findViewById(R.id.txtWebLink)
        btnSave = findViewById(R.id.btnSave)

        getData()
        btnSave.setOnClickListener{
            saveToFirebase()
        }
    }

    private fun initRecyclerView() {
        adapter = WebLinkAdapter()
        apply{
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }
        adapter?.setOnClickView {
//            toast("Firebase ID: ${it.id}")
            txtWebLink.setText(it.webLink.toString())
            openWebLink(this, it.webLink.toString())
            }
        adapter?.setOnLongClickView {
            selectedID = it.id
            showDeleteConfirmationDialog(selectedID.toString())
        }

    }

    fun openWebLink(context: Context, url: String) {
        var webLink: String?=null
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            webLink = "http://$url"
        }
        val webPage: Uri = Uri.parse(webLink)
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("webLink", webLink)
        context.startActivity(intent)
    }

    private fun showDeleteConfirmationDialog(selectedId: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage("Remove item?")
            .setPositiveButton("Yes") { _, _ ->
                // User confirmed deletion
                databaseReference?.child(selectedId.orEmpty())?.removeValue()
                toast("Item has been removed")
            }
            .setNegativeButton("No", null)
            .create()

        alertDialog.show()
    }

    private fun saveToFirebase() {
        val webLink = WebLink(webLink = txtWebLink.text.toString())
        databaseReference?.child(getRandomString(5))?.setValue(webLink)
            ?.addOnSuccessListener {
                toast("Successfully added data to Firebase")
                txtWebLink.text.clear()
            }
            ?.addOnFailureListener {
                toast("Failed to save data to Firebase")
            }

    }

    private fun getData() {
        databaseReference?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.e("hatdog", "onDataChanged: $snapshot")
                list.clear()
                for(ds in snapshot.children){
                    val id = ds.key
                    val link = ds.child("webLink").value.toString()

                    val webLink = WebLink(id = id, webLink = link)
                    list.add(webLink)
                }
                Log.e("hatdog", "size: ${list.size}")

                adapter?.setItems(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("hatdog", "onCancelled: ${error.toException()}")
            }

        })
    }

    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return(1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun toast(message : String){
        val context: Context = applicationContext
        val duration = Toast.LENGTH_SHORT // or Toast.LENGTH_LONG
        val toast = Toast.makeText(context, message, duration)
        toast.show()
    }

}