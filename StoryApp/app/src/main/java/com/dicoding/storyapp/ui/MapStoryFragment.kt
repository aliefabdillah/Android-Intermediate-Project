package com.dicoding.storyapp.ui

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.local.StoryEntity
import com.dicoding.storyapp.databinding.FragmentMapStoryBinding
import com.dicoding.storyapp.models.DbViewModel
import com.dicoding.storyapp.models.MapsViewModel
import com.dicoding.storyapp.models.ViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapStoryFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapStoryBinding
    private lateinit var gMap: GoogleMap
    private val boundsBuilder = LatLngBounds.Builder()

    private val mapsViewModel: MapsViewModel by viewModels { ViewModelFactory.getInstance(requireActivity()) }
    private val dbViewModel: DbViewModel by viewModels { ViewModelFactory.getInstance(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val token = arguments?.getString(TOKEN)
        if (token !=  null){
            dbViewModel.deleteAllData()
            getStoryWithLocation(token)
        }

        dbViewModel.getStories().observe(viewLifecycleOwner){ listStories ->
            createMarker(listStories)
        }

    }

    private fun createMarker(listStories: List<StoryEntity>) {
        listStories.forEach { story ->
            if (story.lat != null && story.lon != null){
                val latLng = LatLng(story.lat, story.lon)
                gMap.addMarker(MarkerOptions().position(latLng).title(story.username).snippet(story.desc))
            }
        }
    }

    private fun getStoryWithLocation(token: String) {
        mapsViewModel.getListStoriesWithLocation(token, 1).observe(viewLifecycleOwner){ result ->
            if (result != null){
                when(result){
                    is Result.Loading -> binding.loadingIcon.visibility = View.VISIBLE
                    is Result.Success -> {
                        if (result.data.isEmpty()) {
                            Toast.makeText(requireActivity(),getString(R.string.list_story_is_empty), Toast.LENGTH_LONG).show()
                        }else{
                            binding.loadingIcon.visibility = View.GONE
                            result.data.forEach {
                                dbViewModel.saveStoryToDb(it)
                            }
                        }
                    }
                    is Result.Error -> {
                        binding.loadingIcon.visibility = View.GONE
                        result.error.getContentIfNotHandled()?.let { toastText ->
                            Toast.makeText(requireActivity(), toastText, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        gMap.uiSettings.isZoomControlsEnabled = true
        gMap.uiSettings.isIndoorLevelPickerEnabled = true
        gMap.uiSettings.isCompassEnabled = true
        gMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()
    }

    private fun setMapStyle() {
        try{
            val success = gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style))

            if(!success) {
                Log.e(TAG, "Style Parsing Failed")
            }
        }catch(exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object{
        const val TOKEN = ""
        const val TAG =  "MapsStoryFragment"
    }
}