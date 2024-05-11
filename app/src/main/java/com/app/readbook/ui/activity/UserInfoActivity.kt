package com.app.readbook.ui.activity

import android.app.Activity
import android.content.Intent
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.app.readbook.App.Companion.user
import com.app.readbook.R
import com.app.readbook.base.BaseVBActivity
import com.app.readbook.data.User
import com.app.readbook.databinding.ActivityUserInfoBinding
import com.google.firebase.firestore.FirebaseFirestore

class UserInfoActivity : BaseVBActivity<ActivityUserInfoBinding>() {

    override fun initView() {
        super.initView()
        binding.textViewUsername.text = user?.name
        // 注册启动Activity的回调
        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // 处理返回结果
                    val data = result.data
                    val username = data?.getStringExtra("username")
                    val age = data?.getStringExtra("age")
                    val gender = data?.getStringExtra("gender")
                    val bio = data?.getStringExtra("bio")
                    val hobbies = data?.getStringExtra("hobbies")
                    val email = data?.getStringExtra("email")

                    // 这里只是简单地显示在 TextView 中，实际应用中可以将信息保存到数据库或服务器
                    findViewById<TextView>(R.id.textViewUsername).text = username
                    findViewById<TextView>(R.id.textViewAge).text = age
                    findViewById<TextView>(R.id.textViewGender).text = gender
                    findViewById<TextView>(R.id.textViewBio).text = bio
                    findViewById<TextView>(R.id.textViewHobbies).text = hobbies
                }
            }
        binding.editBtn.setOnClickListener {
            // 启动Activity
            val intent = Intent(this, EditActivity::class.java)
            startForResult.launch(intent)
        }


    }

    override fun initData() {
        FirebaseFirestore.getInstance().collection("User").whereEqualTo("email", user?.email).get()
            .addOnSuccessListener {
                val userInfo = it.toObjects(
                    User::class.java
                )
                userInfo.sortedByDescending { it.addTime }
                findViewById<TextView>(R.id.textViewUsername).text = userInfo.getOrNull(0)?.name
                findViewById<TextView>(R.id.textViewAge).text = userInfo.getOrNull(0)?.age
                findViewById<TextView>(R.id.textViewGender).text = userInfo.getOrNull(0)?.gender
                findViewById<TextView>(R.id.textViewBio).text = userInfo.getOrNull(0)?.bio
                findViewById<TextView>(R.id.textViewHobbies).text = userInfo.getOrNull(0)?.hobbies

            }.addOnFailureListener {

            }

    }
}