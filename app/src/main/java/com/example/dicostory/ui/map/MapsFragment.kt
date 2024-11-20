package com.example.dicostory.ui.map

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.dicostory.R
import com.example.dicostory.ui.ViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.example.dicostory.data.Result
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private val viewModel: MapViewModel by viewModels{
        ViewModelFactory.getInstance(requireContext())
    }
    private val boundsBuilder = LatLngBounds.Builder()

    private val callback = OnMapReadyCallback {
        it.uiSettings.apply{
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }
        setMapStyle(it)
        setMarker(it)
    }

    private fun setMarker(it: GoogleMap) {
        viewModel.getStoriesWithLocation().observe(viewLifecycleOwner) {result->
            when(result){
                is Result.Success ->{
                    result.data.forEach { story ->
                        val latLng = LatLng(story.lat!!, story.lon!!)
                        it.addMarker(MarkerOptions().position(latLng).title(story.name).snippet(story.description))
                        boundsBuilder.include(latLng)
                    }
                    val bounds: LatLngBounds = boundsBuilder.build()
                    it.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels,
                            300
                        )
                    )
                }
                is Result.Error ->{
                    Log.e("MapsFragment", "Error: ${result.error}")
                }
                else -> {
                    Log.d("MapsFragment", "Loading...")
                }
            }
        }
    }

    private fun setMapStyle(it: GoogleMap) {
        try {
            val success = it.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    companion object {
        const val TAG = "MapsFragment"
    }

}