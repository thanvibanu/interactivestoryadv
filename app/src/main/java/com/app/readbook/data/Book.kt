package com.app.readbook.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

class Book : java.io.Serializable {
    @JvmField

    var id: String = ""
    @JvmField
    var image : Bitmap? = null

    var imageByteArray: ByteArray? = null

    @JvmField
    var imgUri : Uri? = null
    var imgUriString: String? = null

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

    fun setImage_(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        imageByteArray = stream.toByteArray()
    }
    fun getImageBitmap(): Bitmap? {
        return imageByteArray?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
    }

    fun setImgUri_(uri: Uri) {
        imgUriString = uri.toString()
    }

    fun getImgUri(): Uri? {
        return imgUriString?.let { Uri.parse(it) }
    }


}