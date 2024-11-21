package com.example.networkapp

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save comic info when downloaded
// TODO (3: Automatically load previously saved comic when app starts)

const val SAVED_COMIC_TITLE = "saved_comic"
const val SAVED_COMIC_DESCRIPTION = "comic_description"
const val SAVED_COMIC_IMAGE = "comic_image"

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView
    lateinit var comicJson: JSONObject

    lateinit var saveFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        saveFile = File(filesDir,"saveData")

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)
        saveFile

        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }

        if (saveFile.exists()){
            val inputStream = BufferedReader(FileReader(saveFile))
            val savedId = inputStream.readLine()
            if (savedId!=null) {
                downloadComic(savedId)
            }
        }

    }

    // Fetches comic from web as JSONObject
    private fun downloadComic (comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add (
            JsonObjectRequest(url
                , {showComic(it)}
                , {}
            )
        )
    }

    // Display a comic for a given comic JSON object
    private fun showComic (comicObject: JSONObject) {
        comicJson = JSONObject()
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
    }

    override fun onStop() {
        super.onStop()
        try{
            val outputStream = BufferedOutputStream(saveFile.outputStream())
            outputStream.write(numberEditText.text.toString().toByteArray())
            outputStream.close()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

}