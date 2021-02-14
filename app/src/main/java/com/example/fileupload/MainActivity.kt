package com.example.fileupload

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), UploadRequestBody.UploadCallback {

    private var selectedImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        image_view.setOnClickListener{
            openImageChooser()
        }

        button_upload.setOnClickListener{
            uploadImage()
        }
    }

    private fun uploadImage() {
        if (selectedImage == null) {
            layout_root.snackbar("Select an image first")
            return
        }

        val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedImage!!, "r", null) ?: return
        val file =  File(cacheDir, contentResolver.getFileName(selectedImage!!))
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val outPutStream = FileOutputStream(file)
        inputStream.copyTo(outPutStream)

        progress_bar.progress = 0
        val body = UploadRequestBody(file, "image", this)

        MyApi().uploadImage(
            MultipartBody.Part.createFormData("file", file.name, body),
        ).enqueue(object: Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                progress_bar.progress = 100
                layout_root.snackbar(response.body()?.success.toString())
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                layout_root.snackbar(t.message!!)
            }

        })

    }

    private fun openImageChooser(){
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICKER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                REQUEST_CODE_IMAGE_PICKER -> {
                    selectedImage = data?.data
                    image_view.setImageURI(selectedImage)
                }
            }
        }
    }

    companion object{
        private const val REQUEST_CODE_IMAGE_PICKER = 100
    }

    override fun onProgressUpdate(percentage: Int) {
        progress_bar.progress = percentage
    }
}