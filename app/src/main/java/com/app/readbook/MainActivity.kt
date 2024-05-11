package com.app.readbook

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.app.readbook.App.Companion.followList
import com.app.readbook.App.Companion.user
import com.app.readbook.adapter.ViewPagerAdapter
import com.app.readbook.base.BaseVBActivity
import com.app.readbook.data.Book
import com.app.readbook.databinding.ActivityMainBinding
import com.app.readbook.ui.fragment.HomeFragment
import com.app.readbook.ui.fragment.MineFragment
import com.app.readbook.ui.fragment.ShelfFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : BaseVBActivity<ActivityMainBinding>() {
    private lateinit var fragments: MutableList<Fragment>
    private lateinit var adapter: ViewPagerAdapter
    var player: MediaPlayer? = MediaPlayer()
    override fun initView() {
        fragments = mutableListOf()
        fragments.add(HomeFragment())
        fragments.add(ShelfFragment())
        fragments.add(MineFragment())
        if (user?.type == "Writer") {
            binding.bottomNavigationView.menu.removeItem(R.id.nav_shelf)
        }
        adapter = ViewPagerAdapter(this, fragments)
        binding.viewPager.adapter = adapter
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    binding.viewPager.setCurrentItem(0, false)
                    true
                }

                R.id.nav_shelf -> {
                    binding.viewPager.setCurrentItem(1, false)
                    true
                }

                R.id.nav_mine -> {
                    binding.viewPager.setCurrentItem(2, false)
                    true
                }

                else -> false
            }
        }
    }
    override fun initData() {
        super.initData()
        try {
            player?.let { it ->
                val afd = assets.openFd("bgm.mp3");
                it.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length);
                it.prepare()
                it.setOnPreparedListener { media ->
                    media.start()
                }
            }

        } catch (e: Exception) {
            Log.d("TAG", "initData: " + e.message)
        }
        observeBookCounts()
    }

    private fun observeBookCounts() {
        FirebaseFirestore.getInstance().collection("Book").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error listening to book collection: $error")
            }
            var a = 0
            if (snapshot != null) {
                for (document in snapshot) {
                    a++
                }
            }
            if (a != bookCount){
                FirebaseFirestore.getInstance().collection("Book")
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val documents = querySnapshot.documents
                        // 对文档进行排序，这里假设 addTime 是 Long 类型的字段
                        documents.sortByDescending { it.getLong("addTime") ?: 0 }
                        val lastDocument = documents.lastOrNull()

                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error getting documents: ", exception)
                    }

            }
        }

    }


    companion object {
        private const val TAG = "MainActivity"
        var bookCount = 0
    }

    override fun onDestroy() {
        super.onDestroy()
        if (player != null) {
            player?.pause()
            player?.release()
            player = null
        }
    }

}