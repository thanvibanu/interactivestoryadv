package com.app.readbook.data

import com.google.firebase.firestore.DocumentId

class Chapter : java.io.Serializable {
    @JvmField
    var bookId: String = ""

    @JvmField
    @DocumentId
    var id: String = ""

    @JvmField
    var title: String = ""

    @JvmField
    var content: String = ""


    var needHide: Boolean = false

    var openCount: Int = 0

    var addTime: Long = 0

}