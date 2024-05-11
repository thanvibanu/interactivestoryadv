package com.app.readbook.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.createViewModelLazy
import androidx.viewbinding.ViewBinding

abstract class BaseMvvmFragment<VB : ViewBinding, VM : BaseViewModel> :
    BaseVBFragment<VB>() {
    protected lateinit var viewModel: VM private set
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = createViewModelLazy((type.actualTypeArguments[1] as Class<VM>).kotlin, { viewModelStore }).value
        observe()
        super.onViewCreated(view, savedInstanceState)
    }


    /**
     * 订阅，有逻辑的话，复写的时候super不要去掉
     */
    open fun observe() {
        // 需要登录，跳转登录页
        viewModel.needLogin.observe(viewLifecycleOwner) {
            if (it) {

            }
        }
    }
}