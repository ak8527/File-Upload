package com.example.fileupload

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MyApi {

    @Multipart
    @POST("upload")
    fun uploadImage(
        @Part file: MultipartBody.Part
    ): Call<UploadResponse>

    companion object{

        operator fun invoke(): MyApi {
            return Retrofit.Builder()
                .baseUrl("http://ocrv1.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }

}