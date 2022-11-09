package com.dicoding.mygeofence

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.mygeofence.databinding.ActivityMapsBinding
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CircleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val centerLat = -6.861006066605445
    private val centerLng = 107.59418896618537
    private val geofenceRadius = 400.0
    private lateinit var geofencingClient: GeofencingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

    //pending intent untuk menangkap setiap aksi geofence
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        intent.action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }else{
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        // Add a marker in Sydney and move the camera
        val upi = LatLng(centerLat, centerLng)
        mMap.addMarker(MarkerOptions().position(upi).title("Universitas Pendidikan Indonesia"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(upi, 15f))

        mMap.addCircle(CircleOptions()
            .center(upi)
            .radius(geofenceRadius)
            .fillColor(0x22FF0000)
            .strokeColor(Color.RED)
            .strokeWidth(3f)
        )

        getMyLocation()
        addGeofence()
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence() {
        //instance geofence client
        geofencingClient = LocationServices.getGeofencingClient(this)

        //membuat sebuah geofence
        val geofence = Geofence.Builder()
            .setRequestId("kampus")         //id untuk membedakan setiap geofence
            .setCircularRegion(             //menentukan area geofence
                centerLat,
                centerLng,
                geofenceRadius.toFloat()
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)       //menentukan masa kadaluarsa
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_ENTER)   //menetukan aksi apa yang ingin dibaca
            .setLoiteringDelay(5000)    //menentukan delay untuk mengetahui sebuah device telah dwell  pada suatu area
            .build()

        //untuk membuat geofence Request
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)     //menetukan trigeger awal dari device
            .addGeofence(geofence)                      //menambahkan sebuah geofence
            .build()

        //pertama harus menghapus dlu geofence agar tidak terjadi duplikasi
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnCompleteListener {
                //jika berhasil lalu tambahkan geofence baru
                geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
                    addOnSuccessListener {
                        showToast("Geofencing Added")
                    }
                    addOnFailureListener {
                        showToast("Geofencing not added: ${it.message}")
                    }
                }
            }
        }
    }

    //menampilkan toast
    private fun showToast(s: String) {
        Toast.makeText(this@MapsActivity, s, Toast.LENGTH_SHORT).show()
    }

    //request permission background location
    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestBackgroundLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    //requst fine location
    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                //setlah pengecekan biasa lalu menjanlankan pengecekan background
                if (runningQOrLater) {
                    requestBackgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                } else {
                    getMyLocation()
                }
            }
        }

    //chcek permission biasa
    private fun checkPermission(permission: String): Boolean{
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkForegroundLocationPermission(): Boolean{
        val foregroundLocationApproved = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val backgroundPermissionApproved =
            if (runningQOrLater){
                checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }else{
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getMyLocation() {
        if (checkForegroundLocationPermission()){
            mMap.isMyLocationEnabled = true
        }else{
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}