package com.project.veggiekartadmin.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object CloudinaryUploader {

    private const val CLOUD_NAME = "dosrhyslq"
    private const val UPLOAD_PRESET = "veggiekart_preset"
    private const val UPLOAD_URL = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"

    suspend fun uploadImage(context: Context, imageUri: Uri): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                    ?: return@withContext Result.failure(Exception("Cannot read image"))

                val bytes = inputStream.readBytes()
                inputStream.close()

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        "upload.jpg",
                        bytes.toRequestBody("image/*".toMediaTypeOrNull())
                    )
                    .addFormDataPart("upload_preset", UPLOAD_PRESET)
                    .build()

                val request = Request.Builder()
                    .url(UPLOAD_URL)
                    .post(requestBody)
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val json = JSONObject(responseBody)
                    val secureUrl = json.getString("secure_url")
                    Result.success(secureUrl)
                } else {
                    Result.failure(Exception("Upload failed: ${response.code}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}