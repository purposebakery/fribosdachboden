package com.purposebakery.fribosdachboden

import android.Manifest
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.purposebakery.fribosdachboden.data.Data
import com.purposebakery.fribosdachboden.data.Video
import com.purposebakery.fribosdachboden.events.VideoDownloadChangedEvent
import com.purposebakery.fribosdachboden.generic.BaseActivity
import com.purposebakery.fribosdachboden.store.Preferences
import com.purposebakery.fribosdachboden.utils.downloadVideo
import com.purposebakery.fribosdachboden.utils.initVideoFileDownloadedReceiver
import com.purposebakery.fribosdachboden.utils.videoFileBeingDownloaded
import com.purposebakery.fribosdachboden.utils.videoFileDownloaded
import com.sembozdemir.permissionskt.askPermissions
import com.sembozdemir.permissionskt.handlePermissionsResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.browse_activity.*
import kotlinx.android.synthetic.main.content_browse.*
import kotlinx.android.synthetic.main.video_item.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class BrowseActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.browse_activity)

        setSupportActionBar(toolbar)
        downloadAll.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        initGallery()

        askPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            onDenied {
                parent.finish()
            }

            onGranted {
                load()
            }
        }

        initVideoFileDownloadedReceiver(this)
    }

    private fun initGallery() {
        val spanCount = getSpanCount()
        val spacing = resources.getDimension(R.dimen.margin).toInt()
        val includeEdge = true
        videoGallery.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        videoGallery.layoutManager = GridLayoutManager(this, spanCount)
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: VideoDownloadChangedEvent) {
        load()
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
                        Snackbar.make(rootView, R.string.dialog_quality_result_message_hd, Snackbar.LENGTH_LONG).show()
                        load()
                    }
                }
                builder.setNegativeButton(R.string.dialog_quality_mobile) { _, _ ->
                    run {
                        Preferences.setQuality(Preferences.Companion.Quality.MOBILE)
                        Snackbar.make(rootView, R.string.dialog_quality_result_message_mobile, Snackbar.LENGTH_LONG).show()
                        load()
                    }
                }
                builder.show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun load() {
        // Access the RecyclerView Adapter and load the data into it
        videoGallery.adapter = ImageAdapter(Data.getVideos(this), this)
        videoGallery.invalidate()
    }

    private fun getSpanCount(): Int {

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x

        return Math.floor((width.toDouble() - resources.getDimension(R.dimen.margin).toDouble() * 2.toDouble()) / resources.getDimension(R.dimen.video_item_width).toDouble()).toInt()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        handlePermissionsResult(requestCode, permissions, grantResults)
    }

    inner class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }
}

/*
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
*/

class ImageAdapter(private val items: ArrayList<Video>, private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.video_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = items[position]

        holder.videoTitle.text = video.title
        Picasso.get().load(video.thumbnailUrl).into(holder.videoImage)

        holder.download.setOnClickListener { run { downloadVideo(video, context) } }

        if (videoFileDownloaded(context, video)) {
            holder.download.visibility = View.GONE
            holder.downloadProgress.visibility = View.GONE
        } else if (videoFileBeingDownloaded(context, video))  {
            holder.download.visibility = View.GONE
            holder.downloadProgress.visibility = View.VISIBLE
        } else{
            holder.download.visibility = View.VISIBLE
            holder.downloadProgress.visibility = View.GONE
        }
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val videoTitle = view.videoTitle!!
    val videoImage = view.videoImage!!
    val download = view.download!!
    val downloadProgress = view.downloadProgress!!
}
