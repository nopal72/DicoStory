package com.example.dicostory.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicostory.data.local.entity.StoryEntity
import com.example.dicostory.databinding.ItemStoryCardBinding
import com.example.dicostory.ui.detail.DetailActivity
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.util.Pair

class StoryAdapter: ListAdapter<StoryEntity, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(private val binding: ItemStoryCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryEntity){
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = story.createdAt?.let { inputFormat.parse(it) }

            binding.tvName.text = story.name
            binding.tvDescription.text = story.description
            binding.tvDate.text = date?.let { outputFormat.format(it) }
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.ivStory)

            itemView.setOnClickListener {
                val intentDetail = Intent(itemView.context, DetailActivity::class.java)
                val bundle = Bundle()
                bundle.putString("story_id", story.id)
                intentDetail.putExtras(bundle)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivStory, "profile"),
                        Pair(binding.tvName, "name"),
                        Pair(binding.tvDescription, "description"),
                    )

                itemView.context.startActivity(intentDetail,optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
