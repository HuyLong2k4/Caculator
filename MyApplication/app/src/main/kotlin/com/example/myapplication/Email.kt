package com.example.myapplication

data class Email(
    val sender: String,
    val subject: String,
    val preview: String,
    val time: String,
    val avatar: String,
    val avatarColor: String,
    val isSpam: Boolean,
    var isStarred: Boolean
)