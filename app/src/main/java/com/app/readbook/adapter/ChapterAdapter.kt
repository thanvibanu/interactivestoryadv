package com.app.readbook.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.app.readbook.data.Chapter
import com.app.readbook.databinding.ItemChapterBinding

class ChapterAdapter(val onChapterClickListener: (Chapter, Int) -> Unit) :
    BindAdapter<ItemChapterBinding, Chapter>() {

    override fun createHolder(parent: ViewGroup?): ItemChapterBinding {
        return ItemChapterBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun bind(vb: ItemChapterBinding, data: Chapter, position: Int, dataList: List<Chapter>) {
        vb.apply {
            root.text = "Chapter ${position + 1} ${data.title}"
            root.setOnClickListener {
                onChapterClickListener(data, position)
            }

        }
    }
}