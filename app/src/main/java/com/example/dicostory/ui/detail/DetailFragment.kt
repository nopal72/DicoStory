package com.example.dicostory.ui.detail

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.dicostory.databinding.FragmentDetailBinding
import com.example.dicostory.ui.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.example.dicostory.data.Result
import com.example.dicostory.data.remote.response.Story

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val viewModel: DetailViewModel by viewModels{
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDetailBinding.inflate(layoutInflater)

        val bundle = arguments
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

}