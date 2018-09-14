package com.purposebakery.fribosdachboden.data

import android.content.Context
import com.beust.klaxon.JsonArray
import com.beust.klaxon.Klaxon
import com.purposebakery.fribosdachboden.R

class Data {
    companion object {
        fun getVideos(context : Context) : JsonArray<Video>? {

            return Klaxon().parse<JsonArray<Video>>(context.getResources().openRawResource(R.raw.videos))
            
        }

    }
}