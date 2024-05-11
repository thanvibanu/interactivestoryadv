package com.app.readbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.readbook.R
import com.app.readbook.data.Follow

// FollowingAdapter.kt
class FollowingAdapter(private val followingList: List<Follow>) :
    RecyclerView.Adapter<FollowingAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewAuthorName: TextView = itemView.findViewById(R.id.textViewAuthorName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_following, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val authorName = followingList[position]
        holder.textViewAuthorName.text = authorName.writerName
    }

    override fun getItemCount(): Int {
        return followingList.size
    }
}
