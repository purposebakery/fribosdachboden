package com.purposebakery.fribosdachboden.utils

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.purposebakery.fribosdachboden.R
import com.purposebakery.fribosdachboden.data.Video
import com.purposebakery.fribosdachboden.store.Preferences
import java.io.File
import android.content.Intent
import android.content.BroadcastReceiver
import com.purposebakery.fribosdachboden.events.VideoDownloadChangedEvent
import org.greenrobot.eventbus.EventBus

fun initVideoFileDownloadedReceiver(context: Context) {
    context.registerReceiver(onComplete,  IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

}

private var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(ctxt: Context, intent: Intent) {
        EventBus.getDefault().post(VideoDownloadChangedEvent())
    }
}

fun downloadVideo(video: Video, context: Context) {
    val uri = getVideoUri(video)

    val request = DownloadManager.Request(uri)
    request.setDescription(video.title)
    request.setTitle(context.getString(R.string.app_name))
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
    request.allowScanningByMediaScanner()
    request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MOVIES, getFilename(uri))

    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    manager.enqueue(request)

    EventBus.getDefault().post(VideoDownloadChangedEvent())
}

private fun getFilename(uri: Uri): String {
    return uri.toString().substring(uri.toString().lastIndexOf('/') + 1, uri.toString().length)
}

fun videoFileDownloaded(context: Context, video: Video): Boolean {
    return getTargetFile(context, video).exists()
}

fun videoFileBeingDownloaded(context: Context, video: Video) : Boolean {
    val videoDownloadId = Preferences.getVideoDownloadId(video)

    if (videoDownloadId == Preferences.VIDEO_NOT_BEING_DOWNLOADED){
        return false
    } else {
        val query = DownloadManager.Query()
        query.setFilterById(videoDownloadId)
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val cursor = manager.query(query)
        if (cursor.moveToFirst()) {
            if (cursor.count > 0) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if (status == DownloadManager.STATUS_PAUSED ||
                        status == DownloadManager.STATUS_PENDING ||
                        status == DownloadManager.STATUS_RUNNING) {
                    return true
                }
            }
        }
    }

    Preferences.setVideoStoppedDownload(video)
    return false;
}

private fun getVideoUri(video: Video): Uri {
    if (Preferences.getQuality() == Preferences.Companion.Quality.HD) {
        return Uri.parse(video.downloadHdUrl)
    } else {
        return Uri.parse(video.downloadMobileUrl)
    }
}

private fun getTargetFile(context: Context, video: Video): File {
    return File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), getFilename(getVideoUri(video)))
}