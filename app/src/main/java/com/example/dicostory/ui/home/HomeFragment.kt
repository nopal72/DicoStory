package com.example.dicostory.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicostory.R
import com.example.dicostory.data.remote.response.StoryResponse
import com.example.dicostory.databinding.FragmentHomeBinding
import com.example.dicostory.ui.ViewModelFactory
import com.example.dicostory.ui.login.LoginActivity
import com.example.dicostory.data.Result
import com.example.dicostory.data.remote.response.ListStoryItem
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

        viewModel.getSession().observe(viewLifecycleOwner) {
            if (!it.isLogin) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                finishAffinity(requireActivity())
            }
        }

        viewModel.stories.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading ->{
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success ->{
                    binding.progressBar.visibility = View.GONE
                    val story = result.data.listStory
                    setStoryData(story)
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

        binding.btnToPost.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_home_to_navigation_post)
        }

        return binding.root
    }

    private fun setStoryData(story: List<ListStoryItem>) {
        adapter = StoryAdapter()
        adapter.submitList(story)
        binding.rvStory.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}