package com.dicoding.storyapp.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.dicoding.storyapp.models.ViewModelFactory
import com.dicoding.storyapp.utils.createCustomTempFile
import com.dicoding.storyapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private val uploadViewModel: UploadViewModel by viewModels { ViewModelFactory.getInstance(this) }
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

        binding.cameraBtn.setOnClickListener{ startTakePhoto() }
        binding.galleryBtn.setOnClickListener { startGallery() }
        binding.uploadBtn.setOnClickListener { uploadStory() }
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

            dbViewModel.deleteAllData()

            uploadViewModel.getUser().observe(this){ dataStore ->
                uploadCallback(dataStore.token, imageMultipart, desc)
            }
/*            uploadViewModel.getUser().observe(this){ dataStore ->
                uploadViewModel.uploadStory(dataStore.token, imageMultipart, desc)
            }

            uploadViewModel.isLoading.observe(this){
                showLoading(it)
            }

            uploadViewModel.isError.observe(this){ error ->
                if (error){
                    uploadViewModel.toastText.observe(this@UploadActivity){
                        it.getContentIfNotHandled()?.let { toastText ->
                            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    uploadViewModel.toastText.observe(this@UploadActivity){
                        it.getContentIfNotHandled()?.let { toastText ->
                            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()
                        }
                        finish()
                    }
                }
            }*/
        }else{
            Toast.makeText(this, getString(R.string.upload_image_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadCallback(token: String, imageMultipart: MultipartBody.Part, desc: RequestBody) {
        uploadViewModel.uploadStory(token, imageMultipart, desc).observe(this){ result ->
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
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}