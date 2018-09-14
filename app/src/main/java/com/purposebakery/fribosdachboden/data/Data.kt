package com.purposebakery.fribosdachboden.data

class Data {
    companion object {
        fun getVideos() : ArrayList<Video> {
            val videos = ArrayList<Video>()

            videos.add(Video("Familienzeit", "http://www.fribos-dachboden.de/fileadmin/_processed_/csm_0334_0086_familienzeit_tatjana_blank_7df1c06fee.jpg", "https://www.hopechannel.de/fileadmin/videos/2018/DEU_HCTV_0334_0086_013730_20180629_6.mp4", "https://www.hopechannel.de/fileadmin/videos/2018/DEU_HCTV_0334_0086_013730_20180629_6_m.mp4"))

            return videos
        }

    }
}