package com.app.readbook.ui.activity

import android.util.Log
import android.view.View
import com.app.readbook.App
import com.app.readbook.adapter.FollowingAdapter
import com.app.readbook.base.BaseVBActivity
import com.app.readbook.data.Follow
import com.app.readbook.databinding.ActivityFlowListBinding
import com.google.firebase.firestore.FirebaseFirestore

class FlowListActivity : BaseVBActivity<ActivityFlowListBinding>() {
    private lateinit var adapter: FollowingAdapter

    override fun initData() {
        super.initData()
        FirebaseFirestore.getInstance().collection("Flowing").whereEqualTo("userId", App.user?.id)
            .get().addOnSuccessListener {
                val list = it.toObjects(Follow::class.java)
                Log.e("FlowListActivity", "initData: ${list.size}")
                if (!list.isNullOrEmpty()) {
                    binding.tvEmpty.visibility = View.GONE
                    binding.recyclerViewFollowing.visibility = View.VISIBLE
                    adapter = FollowingAdapter(list)
                    binding.recyclerViewFollowing.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                    binding.recyclerViewFollowing.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                }
            }.addOnFailureListener {
                toast("Get following failed")
            }
    }

    override fun initView() {
        super.initView()

    }
}