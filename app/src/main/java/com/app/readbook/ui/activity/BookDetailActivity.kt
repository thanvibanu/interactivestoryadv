package com.app.readbook.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.View
import com.app.readbook.App
import com.app.readbook.App.Companion.user
import com.app.readbook.adapter.ChapterAdapter
import com.app.readbook.base.BaseVBActivity
import com.app.readbook.data.Book
import com.app.readbook.data.Chapter
import com.app.readbook.data.Collect
import com.app.readbook.data.Follow
import com.app.readbook.databinding.ActivityBookDetailBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.IOException

class BookDetailActivity : BaseVBActivity<ActivityBookDetailBinding>() {
    private val adapter: ChapterAdapter by lazy {
        ChapterAdapter { data, position ->
            startActivity(
                if (user?.type == "Writer" && book.email == user?.email) {
                    Intent(this, AddChapterActivity::class.java).putExtra("book", book)
                        .putExtra("chapter", data)
                } else {
                    Intent(this, ReadActivity::class.java).putExtra("chapterList", adapter.data)
                        .putExtra("position", position)
                }

            )
        }
    }
    lateinit var book: Book

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun initData() {
        book = intent.getSerializableExtra("book") as Book
        binding.title.text = book.title
        binding.tvContent.text = book.content
        if (user == null) {
            toast("Please login first")
            startActivity(Intent(this, LoginActivity::class.java))
        }
            runBlocking {
                if(book.getImageBitmap() != null) {
                    binding.imageView.setImageBitmap(book.getImageBitmap())
                }
                else if(book.getImgUri() != null){
                    loadBitmapFromUri(binding.root.context,book.getImgUri()) { bitmap ->
                        // Set the bitmap to the ImageView when it's loaded
                        book.setImage_(bitmap as Bitmap)
                        binding.imageView.setImageBitmap(bitmap as Bitmap?)
                    }
                }
            }
        binding.rvChapter.adapter = adapter
        if (book.writerId == user?.id) {
            binding.tvCollect.visibility = View.GONE
            binding.tvFollow.visibility = View.GONE
            binding.llStatistics.visibility = View.VISIBLE
            binding.tvInfo.text =
                "Number of Readings:${book.readCount}\nNumber of collections:${book.collectCount}"
        } else {
            binding.llStatistics.visibility = View.GONE
            binding.tvIp.visibility = View.GONE
        }
        binding.tvIp.setOnClickListener {
            startActivity(
                Intent(this, AddChapterActivity::class.java).putExtra("book", book)
            )
        }
        FirebaseFirestore.getInstance().collection("Collect").whereEqualTo("bookId", book.id)
            .whereEqualTo("email", user?.email).get().addOnSuccessListener {
                if (it.size() == 0) {
                    binding.tvCollect.text = "Add To Book Shelf"
                } else {
                    binding.tvCollect.text = "Remove From Book Shelf"
                }
            }
        binding.tvCollect.setOnClickListener {
            FirebaseFirestore.getInstance().collection("Collect").whereEqualTo("bookId", book.id)
                .whereEqualTo("email", user?.email).get().addOnSuccessListener {
                    if (it.size() > 0) {
                        FirebaseFirestore.getInstance().collection("Collect")
                            .document(it.documents[0].id).delete()
                        binding.tvCollect.text = "Add To Book Shelf"
                    } else {
                        val collect = Collect()
                        collect.bookId = book.id
                        if (user == null) {
                            toast("Please login first")
                            startActivity(Intent(this, LoginActivity::class.java))
                        }
                        collect.email = user?.email ?: ""
                        FirebaseFirestore.getInstance().collection("Collect").document()
                            .set(collect).addOnSuccessListener {
                                toast("Added to book shelf success")
                            }.addOnFailureListener {
                                toast("Added to book shelf failed")
                            }
                        binding.tvCollect.text = "Remove From Book Shelf"
                        book.collectCount++;
                        FirebaseFirestore.getInstance().collection("Book").document(book.id)
                            .set(book)
                    }
                }
        }

        // 进入检查当前作者是否被关注
        if (user?.id == book.writerId) {
            binding.tvFollow.visibility = View.GONE
        } else {
            FirebaseFirestore.getInstance().collection("Flowing").whereEqualTo("userId", user?.id)
                .whereEqualTo("writerId", book.writerId).get().addOnSuccessListener {
                    if (it.size() == 0){
                        binding.tvFollow.text = "Follow the author"
                    } else {
                        binding.tvFollow.text = "Remove follow"
                    }
                }
            binding.tvFollow.setOnClickListener{
                FirebaseFirestore.getInstance().collection("Flowing").whereEqualTo("userId", user?.id)
                    .whereEqualTo("writerId", book.writerId).get().addOnSuccessListener {
                        if (it.size() == 0) {
                            // 关注相关
                            val follow = Follow()
                            follow!!.userId = user?.id ?: ""
                            follow.writerId = book.writerId
                            follow.bookId = book.id
                            follow.addTime = System.currentTimeMillis()
                            follow.writerName = book.writerName
                            FirebaseFirestore.getInstance().collection("Flowing").document().set(follow)
                                .addOnSuccessListener {toast("Follow success")
                                    binding.tvFollow.text = "Remove follow"
                                }
                                .addOnFailureListener { Log.e("BookDetailActivity", "initView: failed") }

                        } else {
                            FirebaseFirestore.getInstance().collection("Flowing")
                                .document(it.documents[0].id).delete()
                                .addOnSuccessListener { toast("Remove follow success")
                                    binding.tvFollow.text = "Follow the author"
                                }
                                .addOnFailureListener { Log.e("BookDetailActivity", "initView: failed") }
                        }
                    }
            }

        }
    }

    suspend fun loadBitmapFromUri(context: Context, uri: Uri?, param: (Any) -> Unit): Unit? {

        if (uri.toString() != "null" && uri != null) {
            return try {
                val storageRef = Firebase.storage.getReferenceFromUrl(uri.toString())
                val MAX_SIZE_BYTES: Long = 1024 * 1024 // 1MB max
                val imageBytes = storageRef.getBytes(MAX_SIZE_BYTES).await()
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                param(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }

        }
        return null
    }

    override fun onResume() {
        super.onResume()
        FirebaseFirestore.getInstance().collection("Chapter").whereEqualTo("bookId", book.id).get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                val chapters = queryDocumentSnapshots.toObjects(Chapter::class.java)
                    .sortedByDescending { it.addTime }.reversed()
                adapter.data.clear()
                adapter.data.addAll(chapters)
                adapter.notifyDataSetChanged()
            }.addOnFailureListener {
                toast("Get chapter failed")
            }
    }

    override fun initView() {


    }
}