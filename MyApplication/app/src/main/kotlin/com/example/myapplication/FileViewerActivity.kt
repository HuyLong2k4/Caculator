package com.example.myapplication

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class FileViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val filePath = intent.getStringExtra("FILE_PATH")
        val isImage = intent.getBooleanExtra("IS_IMAGE", false)

        if (filePath == null) {
            Toast.makeText(this, "Lỗi: không tìm thấy file", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val file = File(filePath)
        title = file.name

        if (isImage) {
            displayImage(file)
        } else {
            displayText(file)
        }
    }

    private fun displayImage(file: File) {
        setContentView(R.layout.activity_image_viewer)
        val imageView = findViewById<ImageView>(R.id.imageView)

        try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
            } else {
                Toast.makeText(this, "Không thể hiển thị ảnh", Toast.LENGTH_SHORT).show()
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayText(file: File) {
        setContentView(R.layout.activity_text_viewer)
        val textView = findViewById<TextView>(R.id.textView)
        val scrollView = findViewById<ScrollView>(R.id.scrollView)

        try {
            val content = file.readText()
            textView.text = content
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi đọc file: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
