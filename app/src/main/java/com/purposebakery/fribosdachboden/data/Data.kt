package com.purposebakery.fribosdachboden.data

import android.content.Context
import com.beust.klaxon.Klaxon
import com.purposebakery.fribosdachboden.R

object Data {
    fun getVideos(context: Context): ArrayList<Video> {
        val result = ArrayList<Video>()
        val parsed = Klaxon().parseArray<Video>(context.resources.openRawResource(R.raw.videos))

        if (parsed != null) {
            result.addAll(parsed)
        }

        return result
    }
}