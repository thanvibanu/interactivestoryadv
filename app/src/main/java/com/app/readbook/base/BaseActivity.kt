package com.app.readbook.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinging()
        initView()
        initData()
        observe()
        setListener()
    }

    abstract fun initViewBinging()

    /**
     * 初始化view相关
     */
    open fun initView() {}

    /**
     * 初始化data相关
     */
    open fun initData() {}

    /**
     * 设置监听
     */
    open fun setListener() {}

    open fun observe() {}
    protected fun toast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    protected fun log(tag: String = "日志", msg: String?) {
        Log.e(tag, msg ?: " null ")
    }
    fun <T : Activity?> startActivity(tClass: Class<T>?) {
        val intent = Intent(this, tClass)
        startActivity(intent)
    }
}