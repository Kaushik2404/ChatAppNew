package com.example.chatapp.activity

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget

import com.bumptech.glide.request.transition.Transition
import com.example.chatapp.databinding.ActivityFullScreenImageBinding

class FullScreenImageActivity : AppCompatActivity() {
    lateinit var binding: ActivityFullScreenImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityFullScreenImageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra("imageUri")
        Glide.with(this)
            .load(imageUri)
            .into(binding.fullScreenImageView)

        binding.headerImage.back.setOnClickListener {
            finish()
        }

        binding.headerImage.download.setOnClickListener {
            val fileName = "ChatApp${System.currentTimeMillis()}.jpg"
            downloadAndSaveImage(this, imageUri.toString(), fileName)
        }
    }
    fun downloadAndSaveImage(context: Context, imageUrl: String, fileName: String) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    saveImageToGallery(context, resource, fileName)
                }
            })
    }

    // Function to save the image to the device's gallery
    private fun saveImageToGallery(context: Context, bitmap: Bitmap, fileName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
    }
}