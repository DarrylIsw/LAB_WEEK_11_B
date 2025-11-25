package com.example.lab_week_11_b

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import org.apache.commons.io.IOUtils
import java.io.File
import java.util.concurrent.Executor

// Helper class to manage files in MediaStore
class ProviderFileManager(
    private val context: Context,
    private val fileHelper: FileHelper,
    private val contentResolver: ContentResolver,
    private val executor: Executor,
    private val mediaContentHelper: MediaContentHelper
) {

    // Generate the data model (FileInfo) for the file
    // FileInfo contains: uri, file, name, relative path, and MIME type
    fun generatePhotoUri(time: Long): FileInfo {
        val name = "img_$time.jpg"

        // File stored in folder defined in file_provider_paths.xml
        val file = File(
            context.getExternalFilesDir(fileHelper.getPicturesFolder()),
            name
        )

        return FileInfo(
            uri = fileHelper.getUriFromFile(file),
            file = file,
            name = name,
            relativePath = fileHelper.getPicturesFolder(),
            mimeType = "image/jpeg"
        )
    }

    fun generateVideoUri(time: Long): FileInfo {
        val name = "video_$time.mp4"

        val file = File(
            context.getExternalFilesDir(fileHelper.getVideosFolder()),
            name
        )

        return FileInfo(
            uri = fileHelper.getUriFromFile(file),
            file = file,
            name = name,
            relativePath = fileHelper.getVideosFolder(),
            mimeType = "video/mp4"
        )
    }

    // Insert the generated image file into MediaStore
    fun insertImageToStore(fileInfo: FileInfo?) {
        fileInfo?.let {
            insertToStore(
                it,
                mediaContentHelper.getImageContentUri(),
                mediaContentHelper.generateImageContentValues(it)
            )
        }
    }

    // Insert the generated video file into MediaStore
    fun insertVideoToStore(fileInfo: FileInfo?) {
        fileInfo?.let {
            insertToStore(
                it,
                mediaContentHelper.getVideoContentUri(),
                mediaContentHelper.generateVideoContentValues(it)
            )
        }
    }

    // Insert the file into MediaStore (copy file → system media storage)
    private fun insertToStore(
        fileInfo: FileInfo,
        contentUri: Uri,
        contentValues: ContentValues
    ) {
        executor.execute {
            try {
                val insertedUri = contentResolver.insert(contentUri, contentValues)
                if (insertedUri != null) {
                    val inputStream = contentResolver.openInputStream(fileInfo.uri)
                    val outputStream = contentResolver.openOutputStream(insertedUri)

                    if (inputStream != null && outputStream != null) {
                        IOUtils.copy(inputStream, outputStream)
                    }

                    inputStream?.close()
                    outputStream?.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
