package com.purposebakery.fribosdachboden.store

import com.pixplicity.easyprefs.library.Prefs
import com.purposebakery.fribosdachboden.data.Video

class Preferences {
    companion object {
        enum class Quality { MOBILE, HD }

        private val QUALITY_KEY = "QUALITY_KEY"
        private val VIDEO_BEING_DOWNLOADED_KEY = "VIDEO_BEING_DOWNLOADED_KEY"

        public val VIDEO_NOT_BEING_DOWNLOADED : Long = -1

        fun getQuality(): Quality {
            return Quality.valueOf(Prefs.getString(QUALITY_KEY, Quality.MOBILE.name))
        }

        fun setQuality(quality: Quality) {
            Prefs.putString(QUALITY_KEY, quality.name)
        }

        fun getVideoDownloadId(video : Video) : Long {
            return Prefs.getLong(VIDEO_BEING_DOWNLOADED_KEY + video.title, VIDEO_NOT_BEING_DOWNLOADED)
        }

        fun setVideoBeingDownloaded(video : Video, id : Long) {
            Prefs.putLong(VIDEO_BEING_DOWNLOADED_KEY + video.title, id)
        }

        fun setVideoStoppedDownload(video : Video) {
            Prefs.putLong(VIDEO_BEING_DOWNLOADED_KEY + video.title, VIDEO_NOT_BEING_DOWNLOADED)
        }

    }
}