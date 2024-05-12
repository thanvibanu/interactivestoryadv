package com.app.readbook.base

import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.app.readbook.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import com.library.app.authentication.FirebaseAuthenticationManager
import java.lang.reflect.ParameterizedType

abstract class BaseVBActivity<VB : ViewBinding> : BaseActivity() {
    private var _binding: VB? = null
    val db = FirebaseFirestore.getInstance()
    val firebaseAuthManager = FirebaseAuthenticationManager()
    val auth = FirebaseAuth.getInstance()
    val storageRef = Firebase.storage.reference
    protected open val binding
        get() = _binding
            ?: throw IllegalStateException("Cannot access view in after view destroyed and before view creation")
    protected val type = (javaClass.genericSuperclass as ParameterizedType)
    private val classVB = type.actualTypeArguments[0] as Class<VB>
    private val inflateMethod = classVB.getMethod(
        "inflate", LayoutInflater::class.java
    )

    override fun initViewBinging() {
        _binding = inflateMethod.invoke(this, layoutInflater) as VB
        setContentView(_binding!!.root)
        initViewModel()
        val backBtn = findViewById<View>(R.id.backBtn)
        backBtn?.setOnClickListener { finish() }
    }

    open fun initViewModel(){

    }

}