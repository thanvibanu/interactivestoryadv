package com.app.readbook.ui.activity

import android.content.Intent
import com.app.readbook.App
import com.app.readbook.MainActivity
import com.app.readbook.base.BaseMvvmActivity
import com.app.readbook.data.Follow
import com.app.readbook.data.User
import com.app.readbook.databinding.ActivityLoginBinding
import com.app.readbook.util.SpManager
import com.app.readbook.util.SpManager.clearPassword
import com.app.readbook.util.SpManager.clearUsername
import com.app.readbook.util.SpManager.savePassword
import com.app.readbook.util.SpManager.saveUsername
import com.app.readbook.viewmodel.LoginViewModel
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : BaseMvvmActivity<ActivityLoginBinding, LoginViewModel>() {
    override fun initData() {
        binding.loginButton.setOnClickListener {
            val account = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val remember = binding.rememberCheckbox.isChecked
            if (account.isEmpty()) {
                toast("Please enter the email")
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                toast("Please enter password")
                return@setOnClickListener
            }
            FirebaseFirestore.getInstance().collection("User").whereEqualTo("email", account)
                .whereEqualTo("password", password).get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    FirebaseFirestore.getInstance().collection("Flowing").whereEqualTo("userId", App.user?.id)
                        .get().addOnSuccessListener {
                            val followList2 = it.toObjects(
                                Follow::class.java
                            )
                            if (followList2.isNotEmpty()) {
                                App.followList.clear()
                                App.followList.addAll(App.followList)
                            }
                        }
                    val users = queryDocumentSnapshots.toObjects(
                        User::class.java
                    )
                    if (users.isEmpty()) {
                        toast("Account or password error！")
                    } else {
                        if (remember) {
                            saveUsername(this@LoginActivity, account)
                            savePassword(this@LoginActivity, password)
                        } else {
                            clearUsername(this@LoginActivity)
                            clearPassword(this@LoginActivity)
                        }
                        startActivity(MainActivity::class.java)
                        App.login(users[0])
                        finish()
                        toast("Login succeeded")
                    }
                }.addOnFailureListener {
                    toast("Login failed")
                }
        }
        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }
}