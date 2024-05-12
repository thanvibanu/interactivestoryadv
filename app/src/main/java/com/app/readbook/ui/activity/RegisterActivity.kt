package com.app.readbook.ui.activity

import android.util.Log
import android.view.View
import com.app.readbook.App
import com.app.readbook.MainActivity
import com.app.readbook.base.BaseMvvmActivity
import com.app.readbook.data.User
import com.app.readbook.databinding.ActivityRegisterBinding
import com.app.readbook.viewmodel.RegisterViewModel

class RegisterActivity : BaseMvvmActivity<ActivityRegisterBinding, RegisterViewModel>() {
    private val tagname = RegisterActivity::class.simpleName


    override fun initData() {
        binding.registerButton.setOnClickListener(View.OnClickListener {
            Log.d(tagname, "Inside initData()")

            val account = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val rePassword = binding.confirmPasswordEditText.text.toString()
            val name = binding.nameEditText.text.toString()

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

            val user = User()
            user.email = account
            user.password = password
            user.type = binding.typeSpinner.selectedItem.toString()
            user.name = name
            user.id = System.currentTimeMillis().toString()
            user.addTime = System.currentTimeMillis().toString()

            auth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnSuccessListener {
                    Log.d(tagname, "Inside createUserInFirebase addOnSuccessListener()")

                    db.collection("User").add(user).addOnSuccessListener {
                        toast("Registration successful")
                        startActivity(MainActivity::class.java)
                        App.login(user)
                        finish()
                        toast("Login succeeded")
                    }

                }
                .addOnFailureListener {
                    Log.d(tagname, "Inside createUserInFirebase addOnFailureListener()")
                    Log.d(tagname, "Error message: ${it.message}")
                    toast("Registration failed. ${it.message}")
                }

        })
    }
}