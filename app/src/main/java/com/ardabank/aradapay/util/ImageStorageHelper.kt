package com.ardabank.aradapay.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageStorageHelper {

    /**
     * Copies an image from a Content URI to internal storage and returns the local file path / URI.
     */
    fun saveProfileAvatar(context: Context, sourceUri: Uri): String? {
        return try {
            val avatarDir = File(context.filesDir, "avatars").apply { if (!exists()) mkdirs() }
            val destFile = File(avatarDir, "profile_avatar_${System.currentTimeMillis()}.jpg")
            
            // Delete old profile avatars if any to conserve storage
            avatarDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("profile_avatar_") && file != destFile) {
                    file.delete()
                }
            }

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
