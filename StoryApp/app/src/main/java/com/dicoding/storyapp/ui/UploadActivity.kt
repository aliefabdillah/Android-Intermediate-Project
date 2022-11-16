package com.dicoding.storyapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.databinding.ActivityUploadBinding
import com.dicoding.storyapp.models.DbViewModel
import com.dicoding.storyapp.models.UploadViewModel
import com.dicoding.storyapp.modelsfactory.ViewModelFactory
import com.dicoding.storyapp.utils.createCustomTempFile
import com.dicoding.storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val uploadViewModel: UploadViewModel by viewModels { ViewModelFactory.getInstance(this) }

    private var longitude: Double = 0.0
    private var latitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.uploadActivity_title)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.cameraBtn.setOnClickListener{ startTakePhoto() }
        binding.galleryBtn.setOnClickListener { startGallery() }
        binding.uploadBtn.setOnClickListener { uploadStory() }
        binding.setMyLocation.setOnClickListener { myLocation() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()
        ){ permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION]  ?: false -> {
                myLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                myLocation()
            }
            else -> {
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun myLocation(){
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    longitude = location.longitude
                    latitude = location.latitude
                    val address = getAddressName(location.latitude, location.longitude)
                    binding.locationInput.setText(address)
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getAddressName(lat: Double, long: Double): String?{
        var addressName: String? = null
        val geoCoder = Geocoder(this, Locale.getDefault())
        try {
            //menggunakan geocoder
            val list = geoCoder.getFromLocation(lat, long, 1)
            if (list != null && list.size != 0){
                addressName = "${list[0].featureName}, ${list[0].subAdminArea}, ${list[0].adminArea}"
                Log.d(MapStoryFragment.TAG, "getAddressName: $addressName")
            }
        }catch (e: IOException){
            e.printStackTrace()
        }

        return addressName
    }

    private fun uploadStory() {
        val viewModelFactory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val dbViewModel: DbViewModel by viewModels {
            viewModelFactory
        }

        if (getFile != null){
            val file = reduceFileImage(getFile as File)
            val desc = binding.editTextDesc.text.toString().toRequestBody("text/plain".toMediaType())
            val imageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                imageFile
            )

            var lat: Double? = null
            var lon: Double? = null
            if (binding.locationInput.text.isNotEmpty()){
                lat = latitude
                lon = longitude
            }

            dbViewModel.deleteAllData()

            uploadViewModel.getUser().observe(this){ dataStore ->
                uploadCallback(dataStore.token, imageMultipart, desc, lat, lon)
            }
        }else{
            Toast.makeText(this, getString(R.string.upload_image_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadCallback(
        token: String,
        imageMultipart: MultipartBody.Part,
        desc: RequestBody,
        lat: Double?,
        lon: Double?
    ) {
        uploadViewModel.uploadStory(token, imageMultipart, desc, lat, lon).observe(this){ result ->
            if (result != null){
                when (result){
                    is Result.Loading -> binding.loadingIcon.visibility = View.VISIBLE
                    is Result.Success -> {
                        binding.loadingIcon.visibility = View.GONE
                        Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is Result.Error -> {
                        binding.loadingIcon.visibility = View.GONE
                        result.error.getContentIfNotHandled()?.let { toastText ->
                            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        //menentukan tipe data data intent yaitu gambar dengan itpe data apapun
        intent.type = "image/*"
        Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(intent)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        //menampung gambar hasil dari intent Kamera
        createCustomTempFile(application).also {
            //mengambil lokasi file
            val photoURI: Uri = FileProvider.getUriForFile(
                this@UploadActivity,
                "com.dicoding.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }


    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private var getFile: File? = null

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == RESULT_OK){
            //memanggil path file untuk menyimpan hasil capture kamera
            val myFile = File(currentPhotoPath)
            val imageBitmap = BitmapFactory.decodeFile(myFile.path)
            binding.previewImage.setImageBitmap(imageBitmap)

            getFile = myFile
        }
    }

    //mengambil foto dari galeri Intent
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == RESULT_OK){
            //mendapatkan URI dari file yang dipilih
            val selectedImg: Uri = result.data?.data as Uri
            //mengubah uri menjadi bentuk file
            val myFile = uriToFile(selectedImg, this)
            binding.previewImage.setImageURI(selectedImg)

            getFile = myFile
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}