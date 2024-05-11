package com.app.readbook.base

import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

abstract class BaseMvvmActivity<VB : ViewBinding, VM : BaseViewModel> : BaseVBActivity<VB>() {
    protected lateinit var viewModel: VM private set

    override fun initViewModel() {
        viewModel = ViewModelProvider(this)[type.actualTypeArguments[1] as Class<VM>]
    }

    /**
     * 订阅，有逻辑的话，复写的时候super不要去掉
     */
    override fun observe() {
        // 需要登录，跳转登录页
        viewModel.needLogin.observe(this) {
            if (it) {

            }
        }
    }
}