package com.code.presubmission.view.maps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.code.presubmission.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.code.presubmission.databinding.ActivityMapsBinding
import com.code.presubmission.view.ViewModelFactory
import com.code.presubmission.view.welcome.WelcomeActivity

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapsViewModel.getSession().observe(this){result ->
            if(!result.isLogin){
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }else{
                mapsViewModel.getStoriesWithLocation(result.token)
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        addMarkerStory()
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        val mandalika = LatLng(-8.899229863129959, 116.3061502373501)
        mMap.addMarker(MarkerOptions().position(mandalika).title("Mandalika"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mandalika))

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mandalika, 13f))
    }

    private fun addMarkerStory(){
        mapsViewModel.listStoryLocation.observe(this){
            it?.listStory?.forEach{
                val latLng = LatLng(it.lat, it.lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(it.name)
                        .snippet(it.description)
                )
            }
        }
    }

}