package com.app.readbook.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.app.readbook.App
import com.app.readbook.R
import com.app.readbook.base.BaseVBActivity
import com.app.readbook.data.Book
import com.app.readbook.databinding.ActivityAddBookBinding
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID


class AddBookActivity : BaseVBActivity<ActivityAddBookBinding>() {
    private val tagname = AddBookActivity::class.simpleName
    private var id = ""
    private var galleryLauncher: ActivityResultLauncher<Intent>? = null
    private var uploadTask: UploadTask.TaskSnapshot? = null
    override fun initView() {
        var coverImgUri: Uri? = null
        if (App.user == null) {
            toast("Please login first")
            startActivity(Intent(this, LoginActivity::class.java))
        }

        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    findViewById<ImageView>(R.id.cover_image).setImageURI(result.data?.data)
                }
            }

        binding.imageButton.setOnClickListener(View.OnClickListener {
            openGallery()
        })

        binding.tvSave.setOnClickListener(View.OnClickListener {
            val name = binding.etName.text.toString()
            val content = binding.etContent.text.toString()
            val bitmapDrawable = binding.coverImage.drawable as? BitmapDrawable
            val coverImg = bitmapDrawable?.bitmap
            if (name.isEmpty()) {
                toast("Please enter the book name")
                return@OnClickListener
            }
            if (content.isEmpty()) {
                toast("Please enter a description")
                return@OnClickListener
            }
            lifecycleScope.launch {
                if (book == null) {
                    book = Book()
                    book!!.title = name
                    //book!!.image = coverImg
                    book!!.type = binding.spinner.selectedItem.toString();
                    book!!.content = content
                    book!!.email = App.user?.email ?: " "
                    book!!.id = UUID.randomUUID().toString()
                    book!!.writerId = App.user?.id ?: ""
                    book!!.addTime = System.currentTimeMillis()
                    book!!.writerName = App.user?.name ?: ""

                    if (coverImg != null) {
                        coverImgUri = uploadImageToStorage(coverImg)
                        if (coverImgUri != null) {
                            book!!.setImgUri_(coverImgUri!!)
                        }
                    }
                    // Upload successful, proceed with saving the book
                    try {
                        db.collection("Book").document(book!!.id).set(book!!)
                            .addOnSuccessListener {
                                Log.d(tagname, "Book saved successfully")
                                toast("Save successfully")
                            }
                            .addOnFailureListener { exception ->
                                Log.d(tagname, "Failed to save book: ${exception.message}")
                            }
                    } catch (e: Exception) {
                        Log.d(tagname, "Exception while saving book: ${e.message}")
                    }

                } else {
                    if (!id.isEmpty()) {
                        book!!.title = name
                        book!!.content = content
                        book!!.type = binding.spinner.selectedItem.toString();
                        db.collection("Book").document(id).set(book!!)
                            .addOnSuccessListener { finish() }
                    }
                }
                finish()
            }

        })
        binding.tvCancel.setOnClickListener { finish() }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        galleryLauncher?.launch(intent)
    }

    private var book: Book? = null
    override fun initData() {
        book = intent.getSerializableExtra("book") as Book?
        id = book?.id ?: ""
        if (book != null) {
            binding.etName.setText(book!!.title)
            binding.etContent.setText(book!!.content)
        }
    }

    private suspend fun uploadImageToStorage(image: Bitmap?): Uri? {
        val uniqueimagename = UUID.randomUUID()
        val imgref = storageRef.child("images/$uniqueimagename.jpg")
        val imagearray = bitmapToByteArray(image)
        var downloadUrl: Uri? = null
        try {

            if (imagearray != null) {
                uploadTask = imgref.putBytes(imagearray).await()
                downloadUrl = imgref.downloadUrl.await()
            }
        } catch (e: Exception) {
            Log.d(tagname, "Exception in upload ${e.message}")
        }
        return downloadUrl
    }

    private fun bitmapToByteArray(bitmap: Bitmap?): ByteArray? {
        var img: ByteArray? = null
        val compatibleBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        if (compatibleBitmap != null) {
            val outputStream = ByteArrayOutputStream()
            compatibleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            img = outputStream.toByteArray()
        }
        return img
    }
}