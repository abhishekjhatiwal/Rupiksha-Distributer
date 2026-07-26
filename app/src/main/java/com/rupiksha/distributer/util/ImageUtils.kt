package com.rupiksha.distributer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageUtils {
    private const val TAG = "ImageUtils"
    private const val MAX_SIZE_BYTES = 1024 * 1024 // 1MB

    suspend fun compressImage(context: Context, uri: Uri): Uri = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            // Calculate inSampleSize to avoid OOM for very large images
            options.inSampleSize = calculateInSampleSize(options, 2000, 2000)
            options.inJustDecodeBounds = false

            val finalInputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(finalInputStream, null, options)
            finalInputStream?.close()

            if (bitmap == null) return@withContext uri

            var quality = 90
            var byteArray: ByteArray
            val out = ByteArrayOutputStream()
            
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            byteArray = out.toByteArray()

            while (byteArray.size > MAX_SIZE_BYTES && quality > 10) {
                out.reset()
                quality -= 10
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                byteArray = out.toByteArray()
            }

            val file = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { fos ->
                fos.write(byteArray)
            }
            
            Log.d(TAG, "Compressed image size: ${byteArray.size / 1024} KB")
            Uri.fromFile(file)
        } catch (e: Exception) {
            Log.e(TAG, "Error compressing image", e)
            uri // Return original URI if compression fails
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
