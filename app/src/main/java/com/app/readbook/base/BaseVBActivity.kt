package com.app.readbook.base

import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.app.readbook.R
import java.lang.reflect.ParameterizedType

abstract class BaseVBActivity<VB : ViewBinding> : BaseActivity() {
    private var _binding: VB? = null
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