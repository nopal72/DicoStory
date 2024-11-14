package com.example.dicostory.ui.detail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dicostory.data.Result
import com.example.dicostory.data.remote.response.Story
import com.example.dicostory.databinding.ActivityDetailBinding
import com.example.dicostory.ui.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        if (bundle != null) {
            val storyId = bundle.getString("story_id")
            storyId?.let {
                viewModel.getDetail(it).observe(this) {result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val story = result.data.story
                            displayEventDetail(story)
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(
                                binding.root,
                                "Terjadi kesalahan" + result.error,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun displayEventDetail(story: Story) {
        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.ivDetail)

        binding.tvName.text = story.name
        binding.tvDescription.text = story.description
    }
}