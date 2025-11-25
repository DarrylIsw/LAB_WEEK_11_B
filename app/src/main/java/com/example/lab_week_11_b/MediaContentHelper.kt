package com.example.lab_week_11_b

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

// Helper class to generate the URI and ContentValues for MediaStore
class MediaContentHelper {

    // Get the URI to store images in MediaStore
    fun getImageContentUri(): Uri =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

    // Get the URI to store videos in MediaStore
    fun getVideoContentUri(): Uri =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

    // Generate ContentValues to store images in MediaStore
    fun generateImageContentValues(fileInfo: FileInfo) =
        ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileInfo.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, fileInfo.relativePath)
            }
            put(MediaStore.Images.Media.MIME_TYPE, fileInfo.mimeType)
        }

    // Generate ContentValues to store videos in MediaStore
    fun generateVideoContentValues(fileInfo: FileInfo) =
        ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, fileInfo.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.RELATIVE_PATH, fileInfo.relativePath)
            }
            put(MediaStore.Video.Media.MIME_TYPE, fileInfo.mimeType)
        }
}
