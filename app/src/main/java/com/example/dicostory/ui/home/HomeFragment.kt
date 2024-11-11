package com.example.dicostory.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicostory.data.remote.response.StoryResponse
import com.example.dicostory.databinding.FragmentHomeBinding
import com.example.dicostory.ui.ViewModelFactory
import com.example.dicostory.ui.login.LoginActivity

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

        viewModel.getSession().observe(viewLifecycleOwner) {
            if (!it.isLogin) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                finishAffinity(requireActivity())
            }
        }

        viewModel.stories.observe(viewLifecycleOwner) { result ->
            setStoryData(result)
        }

        return binding.root
    }

    private fun setStoryData(result: Result<StoryResponse>?): Result<StoryResponse>? {
        val stories = result?.getOrNull()?.listStory
        if (stories != null) {
            adapter = StoryAdapter()
            adapter.submitList(stories)
            binding.rvStory.adapter = adapter
        }
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}