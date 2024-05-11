package com.app.readbook

import android.app.Application
import android.content.Context
import com.app.readbook.data.Follow
import com.app.readbook.data.User
import kotlin.properties.Delegates

class App : Application() {
    companion object {
        @JvmStatic
        var context: Context by Delegates.notNull()

        private var user_: User? = null

        val user: User?
            get() {
                if (user_ == null)return null
                return user_
            }

        @JvmStatic
        fun login(user: User?) {
            user_ = user
        }

        fun logout() {
            user_ = null
        }

        val followList = ArrayList<Follow>()

    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}