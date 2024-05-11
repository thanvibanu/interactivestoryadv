package com.app.readbook.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import com.app.readbook.App
import com.app.readbook.R
import com.app.readbook.adapter.BindAdapter
import com.app.readbook.base.BaseVBFragment
import com.app.readbook.data.Book
import com.app.readbook.data.Chapter
import com.app.readbook.databinding.FragmentHomeBinding
import com.app.readbook.databinding.ItemMenuBinding
import com.app.readbook.ui.activity.AddBookActivity
import com.app.readbook.ui.activity.BookDetailActivity
import com.app.readbook.ui.activity.LoginActivity
import com.app.readbook.ui.activity.ReadActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : BaseVBFragment<FragmentHomeBinding>() {
    private val adapter: BindAdapter<ItemMenuBinding, Book> =
        object : BindAdapter<ItemMenuBinding, Book>() {
            override fun createHolder(parent: ViewGroup?): ItemMenuBinding {
                return ItemMenuBinding.inflate(layoutInflater, parent, false)
            }

            @SuppressLint("SetTextI18n")
            override fun bind(item: ItemMenuBinding, data: Book, position: Int) {
                item.title.text = data.title
                item.root.setOnClickListener { v: View? ->
                    startActivity(
                        Intent(
                            requireContext(), BookDetailActivity::class.java
                        ).putExtra("book", data)
                    )
                }

                if (App.user?.type == "Writer") {
                    item.tvStartReading.text = "Delete"
                }
                item.tvStartReading.setOnClickListener { v: View? ->
                    if (App.user?.type == "Writer") {
                        AlertDialog.Builder(requireActivity()).setTitle("Notice")
                            .setMessage("Are you sure to delete this book?")
                            .setPositiveButton("Yes") { dialog, which ->
                                FirebaseFirestore.getInstance().collection("Book").document(data.id)
                                    .delete().addOnSuccessListener {
                                        refresh()
                                    }
                            }.setNegativeButton("No") { dialog, which ->
                            }.show()

                    } else {
                        FirebaseFirestore.getInstance().collection("Chapter")
                            .whereEqualTo("bookId", data.id).get()
                            .addOnSuccessListener { queryDocumentSnapshots ->
                                val toObjects =
                                    queryDocumentSnapshots.toObjects(Chapter::class.java)
                                        .sortedByDescending { it.addTime }.reversed()
                                startActivity(
                                    Intent(
                                        requireContext(), ReadActivity::class.java
                                    ).putExtra("chapterList", toObjects as java.io.Serializable)

                                )
                            }.addOnFailureListener { }
                    }

                }
            }
        }
    private val starch: ArrayList<Book> = ArrayList()

    override fun initView() {
        if (App.user == null) {
            startActivity(Intent(activity, LoginActivity::class.java))
        }
        if (App.user?.type == "Reader") {
            binding.tvIp.visibility = View.GONE

        }
        binding.tvIp.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(), AddBookActivity::class.java
                )
            )
        }

        binding.tvStarch.setOnClickListener {
            val text = binding.etSearch.text.toString();
            if (text.isEmpty()) {
                adapter.data.clear()
                adapter.data.addAll(starch)
                adapter.notifyDataSetChanged()
            } else {
                adapter.data.clear()
                for (book in starch) {
                    if (book.title.contains(text) || book.writerName.contains(text)) {
                        adapter.data.add(book)
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private var type2 = ""

    @SuppressLint("NotifyDataSetChanged")
    private fun refresh() {
        FirebaseFirestore.getInstance().collection("Book").apply {
            if (App.user?.type == "Writer") {
                whereEqualTo("email", App.user?.email)
            }
        }.whereEqualTo("type", type2).get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                adapter.data.clear()
                adapter.data.addAll(queryDocumentSnapshots.toObjects(Book::class.java))
                starch.clear()
                starch.addAll(queryDocumentSnapshots.toObjects(Book::class.java))
                adapter.notifyDataSetChanged()
            }.addOnFailureListener { }

    }

    override fun initData() {
        val title = resources.getStringArray(R.array.type)
        for (i in title.indices) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(title[i]))
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                type2 = tab.text.toString()
                refresh()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        binding.rvWorks.adapter = adapter
        type2 = title[0]
        refresh()
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