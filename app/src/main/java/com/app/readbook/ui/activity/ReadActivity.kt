package com.app.readbook.ui.activity

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.app.readbook.adapter.ChapterAdapter
import com.app.readbook.adapter.ReadAdapter
import com.app.readbook.base.BaseVBActivity
import com.app.readbook.data.Book
import com.app.readbook.data.Chapter
import com.app.readbook.databinding.ActivityReadBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

class ReadActivity : BaseVBActivity<ActivityReadBinding>() {

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("chapter", AppCompatActivity.MODE_PRIVATE)
    }
    private lateinit var chapters: List<Chapter>

    private var position: Int = -1
    private val readAdapter: ReadAdapter by lazy {
        ReadAdapter({
            if (binding.drawer.visibility == View.GONE) {
                binding.drawer.visibility = View.VISIBLE
            } else {
                binding.drawer.visibility = View.GONE
            }
        }, {
            if (binding.tvDetail.visibility == View.GONE) {
                binding.tvDetail.visibility = View.VISIBLE
            } else {
                binding.tvDetail.visibility = View.GONE
            }
        })
    }
    private var hideCount = 0
    private var needCount = 0
    @OptIn(DelicateCoroutinesApi::class)
    override fun initData() {
        try {
            chapters = intent.getSerializableExtra("chapterList") as List<Chapter>
        }catch(e : Exception) {
            chapters = deserializeList(intent.getByteArrayExtra("chapterList"))
        }
        setNeedHideInfo(chapters)
        position = intent.getIntExtra("position", sharedPreferences.getInt("position", 0))
        if (!chapters.isNullOrEmpty()) {
            // 增加阅读次数
            FirebaseFirestore.getInstance().collection("Book").document(chapters[position].bookId)
                .get().addOnSuccessListener { it ->
                    val book = Book()
                    book.id = it["id"].toString()
                    book.title = it["title"].toString()
                    book.content = it["content"].toString()
                    book.email = it["email"].toString()
                    book.type = it["type"].toString()
                    book.setImgUri_(Uri.parse(it["imgUri"].toString()))
                    book.writerId = it["writerId"].toString()
                    book.addTime = it["addTime"].toString().toLong()
                    book.readCount = it["readCount"].toString().toInt()
                    book.writerName = it["writerName"].toString()

                    book.let { bookNotNull ->
                        bookNotNull.readCount++
                        FirebaseFirestore.getInstance().collection("Book")
                            .document(chapters[position].bookId).set(bookNotNull)
                    }
                }
        }

        readAdapter.data.addAll(newList)
        binding.viewPager2.adapter = readAdapter
        binding.viewPager2.setCurrentItem(position, false)
        val chapterAdapter = ChapterAdapter { data, position ->
            binding.viewPager2.setCurrentItem(position, false)
            if (binding.drawer.visibility == View.GONE) {
                binding.drawer.visibility = View.VISIBLE
            } else {
                binding.drawer.visibility = View.GONE
            }
        }
        chapterAdapter.data.addAll(newList)
        binding.recyclerView.adapter = chapterAdapter
        var displayedChapters = mutableListOf<Int>()

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("ReadActivity", "onPageSelected: $position")
                this@ReadActivity.position = position
                if (position == needCount && newList.size < chapters.size) {
                    newList.add(chapterItem)
                    newList.sortBy { it.addTime }
                    readAdapter.data.clear()
                    readAdapter.data.addAll(newList)
                    readAdapter.notifyDataSetChanged()
                    chapterAdapter.data.clear()
                    chapterAdapter.data.addAll(newList)
                    chapterAdapter.notifyDataSetChanged()
                }
                displayedChapters.add(position)
                binding.tvDetail.text = "The chapters are read in the order of ${displayedChapters.map { it + 1 }.joinToString()}"

            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.edit().putInt("position", position).apply()
    }

    private val newList = mutableListOf<Chapter>()
    private lateinit var chapterItem: Chapter
    fun setNeedHideInfo(list: List<Chapter>) {
        if (list == null) return
        newList.clear() // 清空旧数据
        for (i in list.indices) {
            if (list[i].needHide) {
                hideCount = i
                needCount = list[i].openCount - 1
                chapterItem = list[i]
            } else {
                newList.add(list[i]) // 不复制，直接添加原始对象到 newList 中
            }
        }
        newList.sortBy { it.addTime } // 按照 addTime 排序 newList
    }

    private fun deserializeList(serializedList: ByteArray?): List<Chapter> {
        if (serializedList == null) return emptyList()
        val byteArrayInputStream = ByteArrayInputStream(serializedList)
        val objectInputStream = ObjectInputStream(byteArrayInputStream)
        @Suppress("UNCHECKED_CAST")
        val list = objectInputStream.readObject() as? List<Chapter>
        objectInputStream.close()
        return list ?: emptyList()
    }
}