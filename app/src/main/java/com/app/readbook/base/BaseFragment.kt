package com.app.base.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    private var lazyLoaded = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        setListener()
    }

    override fun onResume() {
        super.onResume()
        // 实现懒加载
        if (!lazyLoaded) {
            lazyLoadData()
            lazyLoaded = true
        }
    }


    /**
     * 初始化view相关
     */
    abstract fun initView()

    /**
     * 初始化data相关
     */
    open fun initData() {}

    /**
     * 懒加载数据
     */
    open fun lazyLoadData() {}

    /**
     * 设置监听
     */
    open fun setListener() {}
    protected fun toast(msg: String?) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}