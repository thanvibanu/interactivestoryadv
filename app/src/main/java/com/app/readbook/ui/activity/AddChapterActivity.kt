package com.app.readbook.ui.activity

import android.util.Log
import android.view.View
import com.app.readbook.base.BaseVBActivity
import com.app.readbook.data.Book
import com.app.readbook.data.Chapter
import com.app.readbook.databinding.ActivityAddChapterBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddChapterActivity : BaseVBActivity<ActivityAddChapterBinding>() {
    private var chapterId: String? = null
    private var c = 0
    private var count = -1
    override fun initView() {
        binding.tvSave.setOnClickListener(View.OnClickListener {
            val name = binding.etName.text.toString()
            val content = binding.etContent.text.toString()
            count = binding.edOpenTo.text.toString().toIntOrNull() ?: -1
            if (count > c) {
                toast("go beyond Chapter Number")
                return@OnClickListener
            }

            if (name.isEmpty()) {
                toast("Please enter the book name")
                return@OnClickListener
            }
            if (content.isEmpty()) {
                toast("Please enter a description")
                return@OnClickListener
            }
            if (chapter == null) {
                chapter = Chapter()
                chapter!!.title = name
                chapter!!.content = content
                chapter!!.bookId = book.id
                chapter!!.needHide = binding.cbHide.isChecked
                chapter!!.openCount = count
                chapter!!.addTime = System.currentTimeMillis()
                FirebaseFirestore.getInstance().collection("Chapter").document().set(chapter!!)
                    .addOnSuccessListener { finish() }
            } else {
                chapter!!.title = name
                chapter!!.content = content
                chapterId?.let { it1 ->
                    FirebaseFirestore.getInstance().collection("Chapter").document(it1)
                        .set(chapter!!).addOnSuccessListener { finish() }
                }
            }
            toast("Save successfully")
            finish()
        })
        binding.tvCancel.setOnClickListener { finish() }
    }

    private lateinit var book: Book
    private var chapter: Chapter? = null
    override fun initData() {
        book = intent.getSerializableExtra("book") as Book
        FirebaseFirestore.getInstance().collection("Chapter").whereEqualTo("bookId", book.id).get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                val list = queryDocumentSnapshots.toObjects(Chapter::class.java)
                c = list.size
                binding.edOpenTo.hint = "There are currently ${list.size} chapters in total "
            }.addOnFailureListener {
                Log.d("TAG", "onResume: ")
            }
        chapter = intent.getSerializableExtra("chapter") as Chapter?
        chapterId = chapter?.id
        if (chapter != null) {
            binding.etName.setText(chapter!!.title)
            binding.etContent.setText(chapter!!.content)
        }
    }
}