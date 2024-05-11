package com.app.readbook.ui.activity

import android.content.Intent
import android.view.View
import com.app.readbook.App
import com.app.readbook.base.BaseVBActivity
import com.app.readbook.data.Book
import com.app.readbook.databinding.ActivityAddBookBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AddBookActivity : BaseVBActivity<ActivityAddBookBinding>() {
    private var id = ""
    override fun initView() {
        if (App.user == null) {
            toast("Please login first")
            startActivity(Intent(this, LoginActivity::class.java))
        }
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
            if (book == null) {
                book = Book()
                book!!.title = name
                book!!.type = binding.spinner.selectedItem.toString();
                book!!.content = content
                book!!.email = App.user?.email ?: " "
                book!!.id = UUID.randomUUID().toString()
                book!!.writerId = App.user?.id ?: ""
                book!!.addTime = System.currentTimeMillis()
                book!!.writerName = App.user?.name ?: ""
                FirebaseFirestore.getInstance().collection("Book").document(book!!.id).set(book!!)
                    .addOnSuccessListener {
                        finish()
                    }

            } else {
                if (!id.isEmpty()) {
                    book!!.title = name
                    book!!.content = content
                    book!!.type = binding.spinner.selectedItem.toString();
                    FirebaseFirestore.getInstance().collection("Book").document(id).set(book!!)
                        .addOnSuccessListener { finish() }
                }
            }
            toast("Save successfully")
            finish()
        })
        binding.tvCancel.setOnClickListener { finish() }
    }

    private var book: Book? = null
    override fun initData() {
        book = intent.getSerializableExtra("book") as Book?
        id = book?.id ?: ""
        if (book != null) {
            binding.etName.setText(book!!.title)
            binding.etContent.setText(book!!.content)
        }
    }
}