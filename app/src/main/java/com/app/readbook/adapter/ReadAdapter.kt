package com.app.readbook.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.readbook.data.Chapter
import com.app.readbook.databinding.ItemReadBinding
import com.app.readbook.ui.activity.ReadActivity
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream

class ReadAdapter(val onListener: () -> Unit, val onDetailListener: () -> Unit) :
    BindAdapter<ItemReadBinding, Chapter>() {
    override fun createHolder(parent: ViewGroup?): ItemReadBinding {
        return ItemReadBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun bind(vb: ItemReadBinding, data: Chapter, position: Int, dataList: List<Chapter>) {
        vb.tvContent.text = "Chapter ${position + 1} \n${data.title}\n${data.content}"
        setLinkedChapters(data, dataList, vb)
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

    private fun setLinkedChapters(
        data: Chapter,
        dataList: List<Chapter>,
        vb: ItemReadBinding
    ) {
        val chapterNames = data.linkedChapters.joinToString(separator = ", ")
        val builder = SpannableStringBuilder()

        for (chapterName in data.linkedChapters) {
            // Append chapter name to the builder
            builder.append("Select An Adventure:\n")
            builder.append(chapterName)
            builder.append("\n") // Add newline for separation

            // Define a ClickableSpan for the chapter name
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(widget.context, ReadActivity::class.java)
                    val serializedList = serializeList(dataList)
                    var index_ = -1

                    for ((index, chapter) in dataList.withIndex()) {
                        if (chapter.title == chapterName) {
                            index_ = index
                        }
                    }
                    intent.putExtra("chapterList", serializedList)
                    intent.putExtra("position", index_) // Pass the position of the clicked chapter
                    /*intent.putExtra("parent", data.title)
                    intent.putExtra("adventure", chapterName)*/
                    widget.context.startActivity(intent)
                }
            }

            // Find the start and end indices of the chapter name in the SpannableStringBuilder
            val start = builder.length - chapterName.length
            val end = builder.length

            // Set the ClickableSpan to the portion of the text representing the chapter name
            builder.setSpan(
                clickableSpan,
                start,
                end,
                SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        vb.textView.text = builder
        vb.textView.movementMethod = LinkMovementMethod.getInstance()
    }

    fun serializeList(list: List<Any>): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(list)
        objectOutputStream.close()
        return byteArrayOutputStream.toByteArray()
    }
}