package com.dilip.mysignature.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.dilip.mysignature.data.model.Signature
import java.io.File

class SignatureRepository(private val contentResolver: ContentResolver) {

    fun getSavedSignatures(): List<Signature> {
        val signatures = mutableListOf<Signature>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.RELATIVE_PATH
        )
        
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%Pictures/Signatures%")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        try {
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    signatures.add(Signature(contentUri))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Legacy fallback
        val legacyFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Signatures")
        if (legacyFolder.exists()) {
            legacyFolder.listFiles()?.forEach { file ->
                if (file.extension == "png") {
                    val uri = Uri.fromFile(file)
                    if (signatures.none { it.uri == uri }) {
                        signatures.add(Signature(uri))
                    }
                }
            }
        }
        return signatures
    }
}
