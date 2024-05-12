package com.app.readbook.ui.activity

import android.R
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.app.readbook.base.BaseVBActivity
import com.app.readbook.data.Book
import com.app.readbook.data.Chapter
import com.app.readbook.databinding.ActivityAddChapterBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AddChapterActivity : BaseVBActivity<ActivityAddChapterBinding>() {
    private var chapterId: String? = null
    private var c = 0
    private var count = -1
    private var selectedChaptersList = mutableListOf<String>()
    override fun initView() {
        binding.tvSave.setOnClickListener(View.OnClickListener {
            val name = binding.etName.text.toString()
            val content = binding.etContent.text.toString()


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
                chapter!!.id = UUID.randomUUID().toString()
                chapter!!.title = name
                chapter!!.content = content
                chapter!!.bookId = book.id
                chapter!!.needHide = binding.cbHide.isChecked
                chapter!!.openCount = count
                chapter!!.addTime = System.currentTimeMillis()
                FirebaseFirestore.getInstance().collection("Chapter").document().set(chapter!!)
                    .addOnSuccessListener {
                        selectedChaptersList.forEach {str ->
                            val x = db.collection("Chapter").whereEqualTo("title",str.toString()).whereEqualTo("bookId", chapter!!.bookId).get()
                                .addOnSuccessListener {
                                    for(document in it.documents) {
                                        val docRef = db.collection("Chapter").document(document.id)
                                        docRef.update("linkedChapters",FieldValue.arrayUnion(chapter!!.title)).addOnSuccessListener {
                                           Log.d("Testing", " Success")
                                        }
                                            .addOnFailureListener { exception ->
                                                Log.d("Testing", " Failure ${exception.message}")
                                            }
                                    }
                                }
                        }

                        finish() }
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
        db.collection("Chapter").whereEqualTo("bookId", book.id).get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                val list = queryDocumentSnapshots.toObjects(Chapter::class.java)
                c = list.size
                setSpinnerItems(list)
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

    private fun setSpinnerItems(chapters: List<Chapter>) {
        var chapterTitles = mutableListOf<String>("Select A Chapter")
        chapterTitles.addAll(chapters.map { it.title }.sorted())
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, chapterTitles)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(selectedChaptersList.size != chapters.size) {
                    val selectedItem = parent?.getItemAtPosition(position).toString()
                    if(selectedItem != "Select A Chapter" && !selectedChaptersList.contains(selectedItem) && !selectedItem.contains(",")) {
                        selectedChaptersList.add(selectedItem)
                        adapter.clear()
                        adapter.add(selectedChaptersList.joinToString(separator = ", "))
                        val remainingChapters = chapters.filter{!selectedChaptersList.contains(it.title)}.map { it.title }.sorted()
                        adapter.addAll(remainingChapters)
                        binding.spinner.adapter = adapter
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case where no item is selected
            }
        }
    }
}