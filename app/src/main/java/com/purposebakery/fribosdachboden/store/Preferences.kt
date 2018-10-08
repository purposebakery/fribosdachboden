package com.purposebakery.fribosdachboden.store

import com.pixplicity.easyprefs.library.Prefs
import com.purposebakery.fribosdachboden.data.Video

class Preferences {
    companion object {
        enum class Quality { MOBILE, HD }

        private const val QUALITY_KEY = "QUALITY_KEY"
        private const val VIDEO_BEING_DOWNLOADED_KEY = "VIDEO_BEING_DOWNLOADED_KEY"
        private const val DOWNLOAD_LOCKED_KEY = "DOWNLOAD_LOCKED_KEY"

        const val VIDEO_NOT_BEING_DOWNLOADED : Long = -1

        fun getQuality(): Quality {
            return Quality.valueOf(Prefs.getString(QUALITY_KEY, Quality.MOBILE.name))
        }

        fun setQuality(quality: Quality) {
            Prefs.putString(QUALITY_KEY, quality.name)
        }

        fun getVideoDownloadId(video : Video) : Long {
            return Prefs.getLong(VIDEO_BEING_DOWNLOADED_KEY + video.getSourceUri().toString(), VIDEO_NOT_BEING_DOWNLOADED)
        }

        fun setVideoBeingDownloaded(video : Video, id : Long) {
            Prefs.putLong(VIDEO_BEING_DOWNLOADED_KEY + video.getSourceUri().toString(), id)
        }

        fun setVideoStoppedDownload(video : Video) {
            Prefs.putLong(VIDEO_BEING_DOWNLOADED_KEY + video.getSourceUri().toString(), VIDEO_NOT_BEING_DOWNLOADED)
        }

        fun getDownloadLocked() : Boolean {
            return Prefs.getBoolean(DOWNLOAD_LOCKED_KEY, false)
        }

        fun setDownloadLocked(locked : Boolean) {
            Prefs.putBoolean(DOWNLOAD_LOCKED_KEY, locked)
        }
    }
}