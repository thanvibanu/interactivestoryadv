package com.app.readbook.data

import com.google.firebase.firestore.DocumentId

class Book : java.io.Serializable {
    @JvmField

    var id: String = ""


    @JvmField
    var title: String = ""

    @JvmField
    var content: String = ""

    @JvmField
    var type: String = ""

    @JvmField
    var email: String = ""

    @JvmField
    var readCount = 0

    @JvmField
    var collectCount = 0
    @JvmField
    var writerId = ""

    var addTime : Long = 0

    var writerName : String = ""

    override fun hashCode(): Int {
        return id.hashCode()
    }

}