package com.app.readbook.data

import com.google.firebase.firestore.DocumentId
import java.io.Serializable
import java.lang.StringBuilder

class User : Serializable {
    @JvmField
    var type: String= ""

    @JvmField
    var id: String= ""
    @JvmField
    var email: String= ""
    @JvmField
    var name: String= ""
    @JvmField
    var password: String= ""

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("用户ID:").append(id)
        sb.append("邮箱:").append(email).append('\n')
        sb.append("密码:").append(password).append('\n')
        return sb.toString()
    }

    @JvmField
    var age: String = ""
    @JvmField
    var bio: String = ""
    @JvmField
    var hobbies: String = ""
    @JvmField
    var gender: String = ""
    @JvmField
    var addTime: String = ""
}