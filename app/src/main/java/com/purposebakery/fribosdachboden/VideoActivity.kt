package com.purposebakery.fribosdachboden

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.purposebakery.fribosdachboden.generic.BaseActivity

import net.alexandroid.utils.exoplayerhelper.ExoPlayerHelper
import com.purposebakery.fribosdachboden.data.Video
import com.purposebakery.fribosdachboden.utils.NetworkUtils
import kotlinx.android.synthetic.main.video_activity.*

class VideoActivity : BaseActivity() {

    private lateinit var exoPlayerHelper: ExoPlayerHelper

    companion object {
        const val EXTRA_VIDEO = "EXTRA_VIDEO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_activity)

        val video : Video = intent.getParcelableExtra(EXTRA_VIDEO)

        val builder = ExoPlayerHelper.Builder(this, exoPlayerView)
                .setUiControllersVisibility(true)
                .setRepeatModeOn(false)
                .setAutoPlayOn(true)
                .addSavedInstanceState(savedInstanceState)
                .addProgressBarWithColor(ContextCompat.getColor(this, R.color.colorAccent))

        if (video.isVideoFileDownloaded(this)) {
            builder.setVideoUrls(video.getLocalUri(this).toString())
        } else {
            if (NetworkUtils.isNetworkAvailable(this)) {
                builder.setVideoUrls(video.getSourceUri().toString())
            } else {
                showNoNetworkAvailableDialog()
            }
        }

        exoPlayerHelper = builder.createAndPrepare()
    }

    private fun showNoNetworkAvailableDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.generic_error)
        builder.setMessage(R.string.generic_no_network_message)
        builder.setCancelable(false)
        builder.setPositiveButton(R.string.generic_ok) { _: DialogInterface, _: Int -> run { finish()}}
        builder.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        exoPlayerHelper.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        exoPlayerHelper.onActivityStart()
    }

    override fun onResume() {
        super.onResume()
        exoPlayerHelper.onActivityResume()
    }

    override fun onPause() {
        super.onPause()
        exoPlayerHelper.onActivityPause()
    }

    override fun onStop() {
        super.onStop()
        exoPlayerHelper.onActivityStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayerHelper.onActivityDestroy()
    }

}