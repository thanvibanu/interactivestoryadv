package com.app.readbook.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.readbook.data.Chapter
import com.app.readbook.databinding.ItemReadBinding

class ReadAdapter(val onListener: () -> Unit, val onDetailListener: () -> Unit) :
    BindAdapter<ItemReadBinding, Chapter>() {
    override fun createHolder(parent: ViewGroup?): ItemReadBinding {
        return ItemReadBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun bind(vb: ItemReadBinding, data: Chapter, position: Int) {
        vb.tvContent.text = "Chapter ${position + 1} \n${data.title}\n${data.content}"
        vb.btnRead.setOnClickListener {
            onListener()
        }
        if (position == itemCount - 1) {
            vb.btnDetail.visibility = View.VISIBLE
        } else {
            vb.btnDetail.visibility = View.GONE
        }
        vb.btnDetail.setOnClickListener {
            onDetailListener()
        }
    }
}