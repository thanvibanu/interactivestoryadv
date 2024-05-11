package com.app.readbook.ui.activity

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.app.readbook.App.Companion.user
import com.app.readbook.R
import com.app.readbook.base.BaseVBActivity
import com.app.readbook.data.User
import com.app.readbook.databinding.ActivityEditBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditActivity : BaseVBActivity<ActivityEditBinding>() {
    override fun initData() {
        super.initData()
        val spinnerGender: Spinner = findViewById(R.id.spinnerGender)
        ArrayAdapter.createFromResource(
            this, R.array.genders, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGender.adapter = adapter
        }

        val buttonSave: Button = findViewById(R.id.buttonSave)
        buttonSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val username = findViewById<EditText>(R.id.editTextUsername).text.toString()?:""
        val age = findViewById<EditText>(R.id.editTextAge).text.toString()?:""
        val gender = findViewById<Spinner>(R.id.spinnerGender).selectedItem.toString()?:""
        val bio = findViewById<EditText>(R.id.editTextBio).text.toString()?:""
        val hobbies = findViewById<EditText>(R.id.editTextHobbies).text.toString()?:""
        val userInfo = user.apply {
            this?.name = username
            this?.age = age
            this?.gender = gender
            this?.hobbies = hobbies
            this?.bio = bio
            this?.addTime = System.currentTimeMillis().toString()
        }
        val resultIntent = Intent().apply {
            putExtra("username", username)
            putExtra("age", age)
            putExtra("gender", gender)
            putExtra("bio", bio)
            putExtra("hobbies", hobbies)
        }
        userInfo?.let {
            FirebaseFirestore.getInstance().collection("User").add(userInfo).addOnSuccessListener {
                toast("edited successful")
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

    }
}