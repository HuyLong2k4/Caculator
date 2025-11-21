package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmailAdapter(private val emailList: List<Email>) :
    RecyclerView.Adapter<EmailAdapter.EmailViewHolder>() {

    inner class EmailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAvatar: TextView = itemView.findViewById(R.id.tv_avatar)
        private val tvSender: TextView = itemView.findViewById(R.id.tv_sender)
        private val tvSubject: TextView = itemView.findViewById(R.id.tv_subject)
        private val tvPreview: TextView = itemView.findViewById(R.id.tv_preview)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        private val ivSpam: ImageView = itemView.findViewById(R.id.iv_spam)
        private val ivStar: ImageView = itemView.findViewById(R.id.iv_star)

        fun bind(email: Email) {
            tvAvatar.text = email.avatar
            tvAvatar.setBackgroundColor(android.graphics.Color.parseColor(email.avatarColor))
            tvSender.text = email.sender
            tvSubject.text = email.subject
            tvPreview.text = email.preview
            tvTime.text = email.time

            // Hiển thị icon spam
            ivSpam.visibility = if (email.isSpam) View.VISIBLE else View.GONE

            // Xử lý nút star
            updateStarIcon(email)
            ivStar.setOnClickListener {
                email.isStarred = !email.isStarred
                updateStarIcon(email)
            }
        }

        private fun updateStarIcon(email: Email) {
            ivStar.setImageResource(
                if (email.isStarred) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_email, parent, false)
        return EmailViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        holder.bind(emailList[position])
    }

    override fun getItemCount() = emailList.size
}