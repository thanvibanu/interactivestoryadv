package com.app.readbook.data

import android.content.Context
import com.app.readbook.App
import com.app.readbook.util.SpManager

class UserHandler {
    companion object {
        private var currentUser: User? = null

        fun getCurrentUser(context: Context?): User? {
            if (currentUser == null) {
                currentUser = SpManager.getUserData(context)
            }
            return currentUser
        }

    }
}
