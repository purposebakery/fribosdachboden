package com.purposebakery.fribosdachboden.utils

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.*
import android.os.Environment
import android.widget.Toast
import com.purposebakery.fribosdachboden.R
import com.purposebakery.fribosdachboden.data.Data
import com.purposebakery.fribosdachboden.data.Video
import com.purposebakery.fribosdachboden.store.Preferences
import com.purposebakery.fribosdachboden.events.VideoDownloadChangedEvent
import org.greenrobot.eventbus.EventBus

fun initVideoFileDownloadedReceiver(context: Context) {
    context.registerReceiver(onComplete,  IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

}

private var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(ctxt: Context, intent: Intent) {
        EventBus.getDefault().post(VideoDownloadChangedEvent())
    }
}

fun downloadMultipleDialog(context: Context) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(R.string.downloader_download_multiple_dialog_title)
    builder.setMessage(R.string.downloader_download_multiple_dialog_message)
    builder.setPositiveButton(R.string.downloader_download_multiple_dialog_5) { _: DialogInterface, _: Int -> run { downloadMultiple(5, context)}}
    builder.setNegativeButton(R.string.downloader_download_multiple_dialog_10) { _: DialogInterface, _: Int -> run { downloadMultiple(10, context)}}
    builder.show()
}


fun downloadMultiple(amount : Int, context: Context) {
    var count = 0

    for (video in Data.getVideos(context)) {
        if (count == amount) {
            break
        } else if (video.isVideoFileDownloaded(context) || videoFileBeingDownloaded(context, video)) {
            continue
        } else {
            count++
            downloadVideo(video, context)
        }
    }

    if (count == 0) {
        Toast.makeText(context, R.string.downloader_download_multiple_none_left, Toast.LENGTH_LONG).show()
    }
}

fun downloadVideo(video: Video, context: Context) {
    val uri = video.getSourceUri()
    val request = DownloadManager.Request(uri)
    request.setDescription(video.title)
    request.setTitle(context.getString(R.string.app_name))
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
    request.allowScanningByMediaScanner()
    request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MOVIES, video.getLocalFilename())

    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val id = manager.enqueue(request)

    Preferences.setVideoBeingDownloaded(video, id)

    EventBus.getDefault().post(VideoDownloadChangedEvent())
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
    return false
}

