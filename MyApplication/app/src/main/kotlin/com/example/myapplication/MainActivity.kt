package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var rvEmails: RecyclerView
    private lateinit var adapter: EmailAdapter
    private var emailList = mutableListOf<Email>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvEmails = findViewById(R.id.rv_emails)
        setupRecyclerView()
        loadEmails()
    }

    private fun setupRecyclerView() {
        adapter = EmailAdapter(emailList)
        rvEmails.layoutManager = LinearLayoutManager(this)
        rvEmails.adapter = adapter
    }

    private fun loadEmails() {
        emailList.add(Email(
            sender = "Edurila.com",
            subject = "\$19 Only (First 10 spots) - Bestselling...",
            preview = "Are you looking to Learn Web Designin...",
            time = "12:34 PM",
            avatar = "E",
            avatarColor = "#5B9EFF",
            isSpam = true,
            isStarred = false
        ))

        emailList.add(Email(
            sender = "Chris Abad",
            subject = "Help make Campaign Monitor better",
            preview = "Let us know your thoughts! No Images...",
            time = "11:22 AM",
            avatar = "C",
            avatarColor = "#FF7043",
            isSpam = true,
            isStarred = false
        ))

        emailList.add(Email(
            sender = "Tuto.com",
            subject = "8h de formation gratuite et les nouvea...",
            preview = "Photoshop, SEO, Blender, CSS, WordPre...",
            time = "11:04 AM",
            avatar = "T",
            avatarColor = "#66BB6A",
            isSpam = true,
            isStarred = false
        ))

        emailList.add(Email(
            sender = "support",
            subject = "Société Ovh : suivi de vos services - hp...",
            preview = "SAS OVH - http://www.ovh.com 2 rue K...",
            time = "10:26 AM",
            avatar = "S",
            avatarColor = "#78909C",
            isSpam = true,
            isStarred = false
        ))

        emailList.add(Email(
            sender = "Matt from Ionic",
            subject = "The New Ionic Creator Is Here!",
            preview = "Announcing the all-new Creator, buildi...",
            time = "9:45 AM",
            avatar = "M",
            avatarColor = "#66BB6A",
            isSpam = false,
            isStarred = false
        ))

        emailList.add(Email(
            sender = "Matt from Ionic",
            subject = "The New Ionic Creator Is Here!",
            preview = "Announcing the all-new Creator, buildi...",
            time = "9:45 AM",
            avatar = "M",
            avatarColor = "#66BB6A",
            isSpam = false,
            isStarred = false
        ))

        emailList.add(Email(
            sender = "Matt from Ionic",
            subject = "The New Ionic Creator Is Here!",
            preview = "Announcing the all-new Creator, buildi...",
            time = "9:45 AM",
            avatar = "M",
            avatarColor = "#66BB6A",
            isSpam = false,
            isStarred = false
        ))

        emailList.add(Email(
            sender = "Matt from Ionic",
            subject = "The New Ionic Creator Is Here!",
            preview = "Announcing the all-new Creator, buildi...",
            time = "9:45 AM",
            avatar = "M",
            avatarColor = "#66BB6A",
            isSpam = false,
            isStarred = false
        ))

        emailList.add(Email(
            sender = "Matt from Ionic",
            subject = "The New Ionic Creator Is Here!",
            preview = "Announcing the all-new Creator, buildi...",
            time = "9:45 AM",
            avatar = "M",
            avatarColor = "#66BB6A",
            isSpam = false,
            isStarred = false
        ))

        emailList.add(Email(
            sender = "Matt from Ionic",
            subject = "The New Ionic Creator Is Here!",
            preview = "Announcing the all-new Creator, buildi...",
            time = "9:45 AM",
            avatar = "M",
            avatarColor = "#66BB6A",
            isSpam = false,
            isStarred = false
        ))

        adapter.notifyDataSetChanged()
    }
}