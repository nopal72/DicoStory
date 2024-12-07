package com.example.dicostory.ui.post

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.dicostory.R
import com.example.dicostory.databinding.FragmentPostBinding
import com.example.dicostory.ui.ViewModelFactory
import com.example.dicostory.utils.getImageUri
import com.example.dicostory.utils.reduceFileImage
import com.example.dicostory.utils.uriToFile
import com.example.dicostory.data.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class PostFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var binding: FragmentPostBinding
    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.all { it.value }) {
                Toast.makeText(requireContext(), "All permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Some permissions denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentPostBinding.inflate(layoutInflater)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.apply {
            btnGallery.setOnClickListener { startGallery() }
            btnCamera.setOnClickListener { startCamera() }
            buttonAdd.setOnClickListener { uploadStory() }
            locationSwitch.setOnClickListener { getLastLocation() }
        }

        return binding.root
    }

    private fun getLastLocation() {
        if (checkPermission(REQUIRED_PERMISSION_FINE_LOCATION) && checkPermission(REQUIRED_PERMISSION_COARSE_LOCATION))
        {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if(location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                }
                else {
                    showToast("Location is not found. Try Again")
                }
            }
        }else{
            requestPermissionLauncher.launch(
                arrayOf(
                    REQUIRED_PERMISSION_FINE_LOCATION,
                    REQUIRED_PERMISSION_COARSE_LOCATION
                )
            )
        }
    }

    private fun uploadStory() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            val description = binding.edAddDescription.text.toString()
            val lat = latitude
            val lon = longitude

            viewModel.uploadStory(imageFile, description, lat, lon).observe(viewLifecycleOwner){ result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                            true.disableInputFields()
                        }

                        is Result.Success -> {
                            showToast(result.data.message)
                            showLoading(false)
                            false.disableInputFields()
                            findNavController().navigate(R.id.action_navigation_post_to_navigation_home)
                        }

                        is Result.Error -> {
                            showToast(result.error)
                            false.disableInputFields()
                            showLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun Boolean.disableInputFields() {
        binding.buttonAdd.isEnabled = !this
        binding.edAddDescription.isEnabled = !this
        binding.btnCamera.isEnabled = !this
        binding.btnGallery.isEnabled = !this
    }

    private fun startGallery() = launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPost.setImageURI(it)
        }
    }

    private fun startCamera(){
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION_CAMERA = Manifest.permission.CAMERA
        private const val REQUIRED_PERMISSION_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val REQUIRED_PERMISSION_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        private val REQUIRED_PERMISSIONS = arrayOf(
            REQUIRED_PERMISSION_CAMERA,
            REQUIRED_PERMISSION_FINE_LOCATION,
            REQUIRED_PERMISSION_COARSE_LOCATION
        )

    }
}