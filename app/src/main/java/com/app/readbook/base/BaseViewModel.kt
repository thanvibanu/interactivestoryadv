package com.app.readbook.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

typealias Block<T> = suspend (CoroutineScope) -> T
typealias Error = suspend (Exception) -> Unit
typealias Cancel = suspend (Exception) -> Unit

open class BaseViewModel : ViewModel() {
    val needLogin = MutableLiveData<Boolean>().apply { value = false }

    protected fun launch(
        block: Block<Unit>, error: Error? = null, cancel: Cancel? = null,
        // 限制是否通过toast的方式 展示异常
        showErrorToast: Boolean = true
    ): Job {
        return viewModelScope.launch {
            try {
                block.invoke(this)
            } catch (e: Exception) {
                when (e) {
                    //协程异常
                    is CancellationException -> {
                        cancel?.invoke(e)
                        Log.e("日志", "${e.message} " )
                    }

                    else -> {
                        onError(e, showErrorToast)
                        error?.invoke(e)
                    }
                }
            }
        }

    }


    protected fun <T> async(block: Block<T>): Deferred<T> {
        return viewModelScope.async { block.invoke(this) }
    }


    protected fun cancelJob(job: Job?) {
        if (job != null && job.isActive && !job.isCompleted && !job.isCancelled) {
            job.cancel()
        }
    }

    private fun onError(e: Exception, showErrorToast: Boolean) {
        when (e) {

            is ConnectException, is SocketTimeoutException, is UnknownHostException -> {
                Log.e("RIZHI", "onError: $e")
            }

            else -> {
                if (showErrorToast) {

                }
            }
        }
    }
}