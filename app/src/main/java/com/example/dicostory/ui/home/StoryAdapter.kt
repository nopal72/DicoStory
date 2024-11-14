package com.example.dicostory.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicostory.data.remote.response.ListStoryItem
import com.example.dicostory.databinding.ItemStoryCardBinding
import com.example.dicostory.ui.detail.DetailFragment
import java.text.SimpleDateFormat
import java.util.Locale

class StoryAdapter: ListAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(val binding: ItemStoryCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem){

//            set date format
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(story.createdAt)

            binding.tvName.text = story.name
            binding.tvDescription.text = story.description
            binding.tvDate.text = date?.let { outputFormat.format(it) }
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.ivStory)

            itemView.setOnClickListener {
                val intentDetail = Intent(itemView.context, DetailFragment::class.java)
                val bundle = Bundle()
                bundle.putString("id_story", story.id.toString())
                intentDetail.putExtras(bundle)
                itemView.context.startActivity(intentDetail)
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
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}