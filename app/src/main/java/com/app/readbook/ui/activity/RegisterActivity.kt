package com.app.readbook.ui.activity

import android.util.Log
import android.view.View
import com.app.readbook.base.BaseMvvmActivity
import com.app.readbook.data.User
import com.app.readbook.databinding.ActivityRegisterBinding
import com.app.readbook.viewmodel.RegisterViewModel
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : BaseMvvmActivity<ActivityRegisterBinding, RegisterViewModel>() {
    override fun initData() {
        binding.registerButton.setOnClickListener(View.OnClickListener {
            val tagname = RegisterActivity::class.simpleName
            val account = binding.emailEditText.text.toString()
            val password = binding.nameEditText.text.toString()
            val rePassword = binding.passwordEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            Log.d(tagname, "Inside initData()")
            Log.d(tagname, "${account} + ${password} + ${rePassword} + ${name}")
            if (account.isEmpty()) {
                toast("Please enter the email")
                return@OnClickListener
            }
            if (name.isEmpty()) {
                toast("Please enter the name")
                return@OnClickListener
            }
            if (password.isEmpty()) {
                toast("Please enter password")
                return@OnClickListener
            }
            if (rePassword.isEmpty()) {
                toast("Please enter the password again")
                return@OnClickListener
            }
            if (password != rePassword) {
                toast("The two passwords are inconsistent")
                return@OnClickListener
            }
            FirebaseFirestore.getInstance().collection("User").whereEqualTo("email", account)
                .get().addOnSuccessListener { queryDocumentSnapshots ->
                    val users = queryDocumentSnapshots.toObjects(
                        User::class.java
                    )
                    if (users.isEmpty()) {
                        val user = User()
                        user.email = account
                        user.password = password
                        user.type = binding.typeSpinner.selectedItem.toString()
                        user.name = name
                        user.id = System.currentTimeMillis().toString()
                        user.addTime = System.currentTimeMillis().toString()
                        FirebaseFirestore.getInstance().collection("User").add(user)
                            .addOnSuccessListener {
                                toast("Registration successful")
                                finish()
                            }

                    } else {
                        toast("Account already exists")
                    }

                }.addOnFailureListener {
                    toast("Registration failed")
                }

        })


    }
}