package com.app.readbook.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.app.base.fragment.BaseFragment
import java.lang.reflect.ParameterizedType

abstract class BaseVBFragment<VB : ViewBinding> : BaseFragment() {
    private var _binding: VB? = null
    protected open val binding
        get() = _binding
            ?: throw IllegalStateException("Cannot access view in after view destroyed and before view creation")

    protected val type = (javaClass.genericSuperclass as ParameterizedType)
    private val classVB = type.actualTypeArguments[0] as Class<VB>
    private val inflateMethod = classVB.getMethod(
        "inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // 原始的初始化方式
        // binding = ExampleBinding.inflate(inflater)
        _binding = inflateMethod.invoke(null, inflater, container, false) as VB
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}