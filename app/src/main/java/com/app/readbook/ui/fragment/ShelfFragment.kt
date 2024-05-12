package com.app.readbook.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.app.readbook.App
import com.app.readbook.adapter.BindAdapter
import com.app.readbook.base.BaseVBFragment
import com.app.readbook.data.Book
import com.app.readbook.data.Chapter
import com.app.readbook.databinding.FragmentShelfBinding
import com.app.readbook.databinding.ItemMenuBinding
import com.app.readbook.ui.activity.BookDetailActivity
import com.google.firebase.firestore.FirebaseFirestore


class ShelfFragment : BaseVBFragment<FragmentShelfBinding>() {
    private val adapter: BindAdapter<ItemMenuBinding, Book> =
        object : BindAdapter<ItemMenuBinding, Book>() {
            override fun createHolder(parent: ViewGroup?): ItemMenuBinding {
                return ItemMenuBinding.inflate(layoutInflater, parent, false)
            }

            override fun bind(
                item: ItemMenuBinding,
                data: Book,
                position: Int,
                dataList: List<Book>
            ) {
                item.title.text = data.title
                item.root.setOnClickListener { v: View? ->
                    startActivity(
                        Intent(
                            requireContext(), BookDetailActivity::class.java
                        ).putExtra("book", data)
                    )
                }
                item.tvStartReading.setOnClickListener { v: View? ->
                    FirebaseFirestore.getInstance().collection("Chapter")
                        .whereEqualTo("bookId", data.id).get()
                        .addOnSuccessListener { queryDocumentSnapshots ->
                            val toObjects = queryDocumentSnapshots.toObjects(Chapter::class.java)
                            startActivity(
                                Intent(
                                    requireContext(), BookDetailActivity::class.java
                                ).putExtra("book", data)

                            )
                        }.addOnFailureListener {
                            Log.e("ShelfFragment", "onFailure: ", it)
                        }
                }
            }
        }


    override fun initView() {
        binding.rvWorks.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = ShelfFragment()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refresh() {
        FirebaseFirestore
            .getInstance()
            .collection("Collect")
            .whereEqualTo("email", App.user?.email)
            .get().addOnSuccessListener { it ->
                if (it.isEmpty) {
                    return@addOnSuccessListener
                }
                adapter.data.clear()
                it.map { it.get("bookId") }.toMutableList().forEach {
                    FirebaseFirestore.getInstance().collection("Book").document(it.toString()).get()
                        .addOnSuccessListener { queryDocumentSnapshots ->
                            val toObject = queryDocumentSnapshots.toObject(Book::class.java)
                            if (toObject != null && !adapter.data.contains(toObject)) {
                                adapter.data.add(toObject)
                                Log.d("ShelfFragment", "toObject != null: $it")
                                adapter.notifyDataSetChanged()
                            } else {
                                Log.d("ShelfFragment", "onSuccess: $it")
                            }

                        }.addOnFailureListener { 
                            Log.e("ShelfFragment", "onFailure: ", it)
                        }
                }


            }


    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            refresh()
        }
    }
}