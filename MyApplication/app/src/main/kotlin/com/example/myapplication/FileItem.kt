package com.example.myapplication

import java.io.File

data class FileItem(
    val file: File,
    val name: String = file.name,
    val isDirectory: Boolean = file.isDirectory,
    val path: String = file.absolutePath,
    val size: Long = if (file.isFile) file.length() else 0,
    val lastModified: Long = file.lastModified()
) {
    fun getFileExtension(): String {
        return if (isDirectory) "" else name.substringAfterLast(".", "")
    }

    fun isImageFile(): Boolean {
        val ext = getFileExtension().lowercase()
        return ext in listOf("jpg", "jpeg", "png", "bmp", "gif")
    }

    fun isTextFile(): Boolean {
        val ext = getFileExtension().lowercase()
        return ext in listOf("txt", "log", "xml", "json")
    }

    fun getFormattedSize(): String {
        if (isDirectory) return ""
        val kb = size / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0

        return when {
            gb >= 1 -> String.format("%.2f GB", gb)
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> "$size B"
        }
    }
}
