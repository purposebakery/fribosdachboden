package com.purposebakery.fribosdachboden.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Parcelable
import com.purposebakery.fribosdachboden.store.Preferences
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class Video(val title: String, val thumbnailUrl: String, val downloadHdUrl: String, val downloadMobileUrl: String) : Parcelable {

    fun getSourceUri(): Uri {
        return if (Preferences.getQuality() == Preferences.Companion.Quality.HD) {
            Uri.parse(downloadHdUrl)
        } else {
            Uri.parse(downloadMobileUrl)
        }
    }

    fun getLocalUri(context: Context): Uri {
        return Uri.fromFile(getLocalFile(context))
    }

    fun getLocalFilename(): String {
        val uri =  getSourceUri()
        return uri.toString().substring(uri.toString().lastIndexOf('/') + 1, uri.toString().length)
    }

    fun isVideoFileDownloaded(context: Context): Boolean {
        return getLocalFile(context).exists()
    }

    private fun getLocalFile(context: Context): File {
        return File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), getLocalFilename())
    }
}