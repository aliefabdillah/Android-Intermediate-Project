package com.dicoding.mylocationtracker

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.mylocationtracker.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.util.concurrent.TimeUnit

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var isTracking = false
    private var allLatLng = ArrayList<LatLng>()
    private var boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //membuat instance fusedLocationClient untuk mendapatkan data lokasi
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        createLocationRequest()
        createLocationCallback()

        //action button ketika diclikc
        binding.btnStart.setOnClickListener {
            if (!isTracking){
                clearMaps()                         //mereset maps agar tidak terjadi tumpang tindih garis route
                updateTrackingStatus(true)
                startLocationUpdates()
            }else{
                updateTrackingStatus(false)
                stopLocationUpdates()
            }
        }
    }

    //method menghapus map setiap tombol running ditekan
    private fun clearMaps() {
        mMap.clear()
        allLatLng.clear()
        boundsBuilder = LatLngBounds.Builder()
    }

    //mengganti text button
    private fun updateTrackingStatus(status: Boolean) {
        isTracking = status
        if (isTracking){
            binding.btnStart.text = getString(R.string.stop_running)
        }else{
            binding.btnStart.text = getString(R.string.start_running)
        }
    }

    //permission lokasi kepada user
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){ permissions ->
            when{
                permissions[Manifest.permission.ACCESS_FINE_LOCATION]  ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                }
            }
        }

    //check permission apakah sudah di setujui atau tidak
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission,
        ) == PackageManager.PERMISSION_GRANTED
    }

    //mengambil lokasi terakhir devices
    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
        {
            //mengakes variabel lastLocation
            /*
            * location dapat bernilai null jika gps dimatikan dan cachce berupa data lokasi dihapus, ketika device masih baru atau direset, atau Google play service terrestart, */
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null){
                    showStartMarker(location)
                }else{
                    Toast.makeText(this, "Location is not found. Try Again", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    //menampilkan start marker
    private fun showStartMarker(location: Location) {
        val startLocation = LatLng(location.latitude, location.longitude)
        mMap.addMarker(
            MarkerOptions()
                .position(startLocation)
                .title(getString(R.string.start_point))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 17f))
    }

    //resolution launcher untuk menangani gagal mengambil locationRequst
    private val resolutionLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()
        ){result ->
            when(result.resultCode){
                RESULT_OK ->
                    Log.i(TAG, "onActivityResult: All location settings are satisfied.")
                RESULT_CANCELED ->
                    Toast.makeText(
                        this@MapsActivity,
                        "Anda harus mengaktifkan GPS untuk menggunakan aplikasi ini!",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }

    private fun createLocationRequest() {
        //mengatur request saaat konfigurasi lokasi
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)         //Mengetahui berapa interval untuk mengambil data lokasi kembali dalam milidetik.
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)      //Mengatur waktu maksimal untuk update lokasi dalam milidetik.
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY       //Mengatur prioritas untuk menentukan sumber data. Semakin tinggi akan semakin akurat dan menghabiskan baterai.
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)

        //mengecek kondisi location requst sudah terpenuhi atau belum
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener { exception ->
                //jika gagal menyerahkan ke sistem untuk menampilkan pop up untuk menyalakan gps
                if (exception is ResolvableApiException){
                    try {
                        resolutionLauncher.launch(IntentSenderRequest.Builder(exception.resolution).build())
                    }catch (sendEx: IntentSender.SendIntentException){
                        Toast.makeText(this, sendEx.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun createLocationCallback() {
        locationCallback = object  : LocationCallback(){
            //onLocationResult dipanggil ketika aplikasi berhasil mendapatkan data lokasi
            override fun onLocationResult(locationRes: LocationResult) {
                for (location in locationRes.locations){
                    Log.d(TAG, "OnLocationResult: " + location.latitude + ", " + location.longitude)
                    val lastLatLng = LatLng(location.latitude, location.longitude)

                    //menggambar garis rute
                    allLatLng.add(lastLatLng)
                    mMap.addPolyline(
                        PolylineOptions()
                            .color(Color.CYAN)
                            .width(10f)
                            .addAll(allLatLng)


                    )
                    //set Boundaries atau tampilan maps mengikuti posisi devices
                    boundsBuilder.include(lastLatLng)
                    val bounds: LatLngBounds = boundsBuilder.build()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))
                }
            }
        }
    }

    private fun startLocationUpdates(){
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }catch (exception: SecurityException) {
            Log.e(TAG, "Error : " + exception.message)
        }
    }

    private fun stopLocationUpdates(){
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (isTracking){
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}