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
        try {
            val bitmap = Glide.with(mContext)
                .asBitmap()
                .load(story.photoUrl)
                .override(300,300)
                .submit()
                .get()
            rv.setImageViewBitmap(R.id.imageView, bitmap)
        } catch (e: Exception) {
            rv.setImageViewResource(R.id.imageView, R.drawable.ic_image_place_holder)
        }
        rv.setTextViewText(R.id.banner_text, story.name)

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}