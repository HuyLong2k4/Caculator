package com.example.myapplication

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fileAdapter: FileAdapter
    private var currentDirectory: File = Environment.getExternalStorageDirectory()
    private val PERMISSION_REQUEST_CODE = 100
    private var selectedDirectoryForCopy: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fileAdapter = FileAdapter(
            emptyList(),
            onItemClick = { fileItem -> onFileItemClick(fileItem) },
            onItemLongClick = { fileItem, view -> onFileItemLongClick(fileItem, view) }
        )
        recyclerView.adapter = fileAdapter

        if (checkPermissions()) {
            loadDirectory(currentDirectory)
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val readPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            val writePermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            readPermission && writePermission
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, PERMISSION_REQUEST_CODE)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, PERMISSION_REQUEST_CODE)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadDirectory(currentDirectory)
            } else {
                Toast.makeText(this, "Cần cấp quyền để sử dụng ứng dụng", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (checkPermissions()) {
                loadDirectory(currentDirectory)
            } else {
                Toast.makeText(this, "Cần cấp quyền để sử dụng ứng dụng", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadDirectory(directory: File) {
        currentDirectory = directory
        title = directory.absolutePath

        try {
            val files = directory.listFiles()?.map { FileItem(it) }
                ?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
                ?: emptyList()

            fileAdapter.updateList(files)
        } catch (e: Exception) {
            Toast.makeText(this, "Không thể đọc thư mục: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onFileItemClick(fileItem: FileItem) {
        if (fileItem.isDirectory) {
            loadDirectory(fileItem.file)
        } else {
            if (fileItem.isTextFile() || fileItem.isImageFile()) {
                val intent = Intent(this, FileViewerActivity::class.java)
                intent.putExtra("FILE_PATH", fileItem.path)
                intent.putExtra("IS_IMAGE", fileItem.isImageFile())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Không hỗ trợ xem loại file này", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onFileItemLongClick(fileItem: FileItem, view: View): Boolean {
        registerForContextMenu(view)
        openContextMenu(view)
        unregisterForContextMenu(view)

        // Lưu fileItem để sử dụng trong context menu
        view.tag = fileItem
        return true
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val fileItem = v.tag as? FileItem ?: return

        if (fileItem.isDirectory) {
            menu.setHeaderTitle(fileItem.name)
            menu.add(0, 1, 0, "Đổi tên")
            menu.add(0, 2, 0, "Xóa")
        } else {
            menu.setHeaderTitle(fileItem.name)
            menu.add(0, 3, 0, "Đổi tên")
            menu.add(0, 4, 0, "Xóa")
            menu.add(0, 5, 0, "Sao chép")
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val view = findViewById<View>(android.R.id.content)
        val fileItem = view.findViewWithTag<View>(null)?.tag as? FileItem

        // Tìm view được long click gần nhất
        var targetView: View? = null
        recyclerView.forEach { child ->
            if (child.tag is FileItem) {
                targetView = child
            }
        }
        val selectedFile = targetView?.tag as? FileItem ?: return false

        when (item.itemId) {
            1, 3 -> renameFile(selectedFile) // Đổi tên
            2, 4 -> deleteFile(selectedFile) // Xóa
            5 -> copyFile(selectedFile) // Sao chép
        }
        return super.onContextItemSelected(item)
    }

    private fun RecyclerView.forEach(action: (View) -> Unit) {
        for (i in 0 until childCount) {
            action(getChildAt(i))
        }
    }

    private fun renameFile(fileItem: FileItem) {
        val input = EditText(this)
        input.setText(fileItem.name)

        AlertDialog.Builder(this)
            .setTitle("Đổi tên")
            .setMessage("Nhập tên mới:")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val newName = input.text.toString()
                if (newName.isNotBlank()) {
                    val newFile = File(fileItem.file.parent, newName)
                    if (fileItem.file.renameTo(newFile)) {
                        Toast.makeText(this, "Đã đổi tên thành công", Toast.LENGTH_SHORT).show()
                        loadDirectory(currentDirectory)
                    } else {
                        Toast.makeText(this, "Đổi tên thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteFile(fileItem: FileItem) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa ${fileItem.name}?")
            .setPositiveButton("Xóa") { _, _ ->
                if (deleteRecursive(fileItem.file)) {
                    Toast.makeText(this, "Đã xóa thành công", Toast.LENGTH_SHORT).show()
                    loadDirectory(currentDirectory)
                } else {
                    Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteRecursive(fileOrDirectory: File): Boolean {
        if (fileOrDirectory.isDirectory) {
            fileOrDirectory.listFiles()?.forEach { child ->
                deleteRecursive(child)
            }
        }
        return fileOrDirectory.delete()
    }

    private fun copyFile(fileItem: FileItem) {
        selectedDirectoryForCopy = fileItem.file
        Toast.makeText(
            this,
            "Đã chọn file. Vào thư mục đích và chọn 'Dán' từ menu",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_create_folder -> {
                createNewFolder()
                true
            }
            R.id.action_create_file -> {
                createNewTextFile()
                true
            }
            R.id.action_paste -> {
                pasteFile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createNewFolder() {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Tạo thư mục mới")
            .setMessage("Nhập tên thư mục:")
            .setView(input)
            .setPositiveButton("Tạo") { _, _ ->
                val folderName = input.text.toString()
                if (folderName.isNotBlank()) {
                    val newFolder = File(currentDirectory, folderName)
                    if (newFolder.mkdir()) {
                        Toast.makeText(this, "Đã tạo thư mục thành công", Toast.LENGTH_SHORT).show()
                        loadDirectory(currentDirectory)
                    } else {
                        Toast.makeText(this, "Tạo thư mục thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun createNewTextFile() {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Tạo file văn bản mới")
            .setMessage("Nhập tên file (không cần .txt):")
            .setView(input)
            .setPositiveButton("Tạo") { _, _ ->
                val fileName = input.text.toString()
                if (fileName.isNotBlank()) {
                    val newFile = File(currentDirectory, "$fileName.txt")
                    try {
                        if (newFile.createNewFile()) {
                            Toast.makeText(this, "Đã tạo file thành công", Toast.LENGTH_SHORT).show()
                            loadDirectory(currentDirectory)
                        } else {
                            Toast.makeText(this, "File đã tồn tại", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Tạo file thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun pasteFile() {
        val fileToCopy = selectedDirectoryForCopy
        if (fileToCopy == null) {
            Toast.makeText(this, "Chưa chọn file để sao chép", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Xác nhận sao chép")
            .setMessage("Sao chép ${fileToCopy.name} vào thư mục này?")
            .setPositiveButton("Sao chép") { _, _ ->
                try {
                    val destFile = File(currentDirectory, fileToCopy.name)
                    if (destFile.exists()) {
                        Toast.makeText(this, "File đã tồn tại", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    FileInputStream(fileToCopy).use { input ->
                        FileOutputStream(destFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    Toast.makeText(this, "Đã sao chép thành công", Toast.LENGTH_SHORT).show()
                    loadDirectory(currentDirectory)
                    selectedDirectoryForCopy = null
                } catch (e: Exception) {
                    Toast.makeText(this, "Sao chép thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    override fun onBackPressed() {
        val parentDir = currentDirectory.parentFile
        if (parentDir != null && currentDirectory != Environment.getExternalStorageDirectory()) {
            loadDirectory(parentDir)
        } else {
            super.onBackPressed()
        }
    }
}