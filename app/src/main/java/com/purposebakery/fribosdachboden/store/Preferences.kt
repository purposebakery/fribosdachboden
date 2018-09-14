package com.purposebakery.fribosdachboden.store

import com.pixplicity.easyprefs.library.Prefs

class Preferences {
    companion object {
        enum class Quality {MOBILE, HD}

        private val QUALITY_KEY = "QUALITY_KEY"

        fun getQuality () : Quality {
            return Quality.valueOf(Prefs.getString(QUALITY_KEY, Quality.MOBILE.name))
        }

        fun setQuality (quality: Quality) {
            Prefs.putString(QUALITY_KEY, quality.name)
        }

    }
}