package com.example.dicostory.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.dicostory.R
import com.example.dicostory.data.local.entity.StoryEntity
import com.example.dicostory.data.local.room.StoryDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(
    private val mContext: Context
) : RemoteViewsService.RemoteViewsFactory {

    private val mImageUrls = ArrayList<String>()
    private val mWidgetItems = ArrayList<Bitmap>()
    private var stories: List<StoryEntity> = emptyList()

    override fun onCreate() {
        loadData()
    }

    override fun onDataSetChanged() {
        loadData()
    }

    override fun onDestroy() {
        mImageUrls.clear()
    }

    private fun loadData() {
        val database = StoryDatabase.getInstance(mContext)
        val storyDao = database.storyDao()
        runBlocking {
            stories = storyDao.getStories()
            stories.forEach { story ->
                Log.d("Widget", "Story Loaded: ${story.name}, Photo URL: ${story.photoUrl}")
            }
        }
    }

    override fun getCount(): Int = stories.size

    override fun getViewAt(p0: Int): RemoteViews {
        if (stories.isEmpty() || p0 >= stories.size) return RemoteViews(mContext.packageName, R.layout.widget_item)

        val story = stories[p0]
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        val bitmap = Glide.with(mContext)
            .asBitmap()
            .load(story.photoUrl)
            .submit()
            .get()
        rv.setTextViewText(R.id.banner_text, story.name)
        rv.setImageViewBitmap(R.id.imageView, bitmap)

        // Tambahkan PendingIntent untuk interaksi jika diperlukan
        val extras = bundleOf("story_id" to story.id)
        val fillInIntent = Intent().putExtras(extras)
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = i.toLong()

    override fun hasStableIds(): Boolean = true
}