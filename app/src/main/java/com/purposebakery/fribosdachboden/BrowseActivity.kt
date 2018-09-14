package com.purposebakery.fribosdachboden

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.Log
import android.view.*
import android.widget.BaseAdapter
import android.widget.Switch
import com.purposebakery.fribosdachboden.data.Data
import com.purposebakery.fribosdachboden.data.Video
import com.purposebakery.fribosdachboden.generic.BaseActivity
import com.purposebakery.fribosdachboden.store.Preferences
import com.sembozdemir.permissionskt.askPermissions
import com.sembozdemir.permissionskt.handlePermissionsResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.browse_activity.*
import kotlinx.android.synthetic.main.content_browse.*

class BrowseActivity : BaseActivity () {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.browse_activity)

        setSupportActionBar(toolbar)
        downloadAll.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        askPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            onDenied {
                parent.finish()
            }

            onGranted {
                load()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_browse, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_quality_item -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.dialog_quality_title)
                builder.setMessage(R.string.dialog_quality_message)
                builder.setPositiveButton(R.string.dialog_quality_hd) { _, _ ->
                    run {
                        Preferences.setQuality(Preferences.Companion.Quality.HD)
                        Snackbar.make(rootView, R.string.dialog_quality_result_message_hd, Snackbar.LENGTH_SHORT).show()
                    }
                }
                builder.setNeutralButton(R.string.dialog_quality_mobile) { _, _ ->
                    run {
                        Preferences.setQuality(Preferences.Companion.Quality.MOBILE)
                        Snackbar.make(rootView, R.string.dialog_quality_result_message_mobile, Snackbar.LENGTH_SHORT).show()
                    }
                }
                builder.show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun load() {
        videoGallery.adapter = ImageAdapter(this, Data.getVideos())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        handlePermissionsResult(requestCode, permissions, grantResults)
    }

    fun downloadByDownloadManager(video : Video, hd : Boolean) {
        var uri = Uri.EMPTY
        if (hd) {
            uri = Uri.parse(video.downloadHdUrl)
        } else {
            uri = Uri.parse(video.downloadMobileUrl)
        }

        val request = DownloadManager.Request(uri)
        request.setDescription("A zip package with some files")
        request.setTitle("Zip package")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.allowScanningByMediaScanner()
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_MOVIES, getFilename(uri))

        Log.d("MainActivity: ", "download folder>>>>" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath())

        // get download service and enqueue file
        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }

    private fun getFilename(uri : Uri) : String {
        return uri.toString().substring(uri.toString().lastIndexOf('/') + 1, uri.toString().length)
    }
}

class ImageAdapter(private val context: Context, private val videos : ArrayList<Video>) : BaseAdapter() {

    override fun getCount(): Int = videos.size

    override fun getItem(position: Int): Video = videos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val video = getItem(position)

        val view: View
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false)
        } else view = convertView

        view.findViewById<AppCompatTextView>(R.id.videoTitle).text = video.title

        Picasso.get().load(video.thumbnailUrl).into(view.findViewById<AppCompatImageView>(R.id.videoImage))

        return view
    }
}
