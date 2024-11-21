package com.example.dicostory.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicostory.R
import com.example.dicostory.databinding.FragmentHomeBinding
import com.example.dicostory.ui.ViewModelFactory
import com.example.dicostory.data.Result
import com.example.dicostory.data.local.entity.StoryEntity
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(requireContext())
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)

        binding.rvStory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStory.addItemDecoration(itemDecoration)

        setStoryData()

        binding.btnToPost.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_home_to_navigation_post)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.story
            binding.swipeRefresh.isRefreshing = false
        }

        return binding.root
    }

    private fun setStoryData() {
        adapter = StoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        viewModel.story.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }
    }

}