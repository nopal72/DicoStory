package com.example.dicostory.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.dicostory.databinding.FragmentHomeBinding
import com.example.dicostory.ui.ViewModelFactory
import com.example.dicostory.ui.login.LoginActivity

class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        viewModel.getSession().observe(viewLifecycleOwner) {
            if (!it.isLogin) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                finishAffinity(requireActivity())
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}