package com.dicoding.picodiploma.mycamera

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dicoding.picodiploma.mycamera.databinding.ActivityMainBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    //method request permissions aplikasi untuk mengakses untuk kamera
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (!allPermissionsGranted()){
                Toast.makeText(this, "Tidak mendapatkan permissions.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    //mengompress file image
    private fun reduceFileImage(file: File): File{
        //mengubah file menjadi bitmap
        val bitmap = BitmapFactory.decodeFile(file.path)
        //menyiapkan nilai untuk compressQuality yang nantinya nilainya akan berkurang seiring ukuran file yang beesar
        var compressQuality = 100
        //variabel streamLentgh berguna untuk menampung ukuran bitmap agara sesuai yang diinginkan
        var streamLength: Int

        //perulangan untuk mengurangi nilainya sampai dibawah 1 mb
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPictByteArray = bmpStream.toByteArray()
            streamLength = bmpPictByteArray.size
            compressQuality -= 5
        }while (streamLength > 1000000)
        //memasukan bitmpat ke dalam fileOuputStream setelah ukurannya kurang dari 1 mb
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

        //return file yang telah di compress
        return file
    }

    //fungsi upload image
    private fun uploadImage() {
        if (getFile != null){
            //mereduce ukuran image menjadi dibawah 1 mb(khusus dari real device)
            val file = reduceFileImage(getFile as File)

            //mengubah data menjadi requestBody
            val description = "Ini adalah deskripsi gambar".toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

            //membuat form data yang dibutuhkan oleh endPoint
            /*
            * terdapat 3 argument
            - name : Sebagai kata kunci yang sesuai dengan endpoint.
            -filename : Memberikan nama pada file yang akan dikirim.
            -body : Melampirkan file yang sudah dibungkus dalam bentuk Request Body.
            * */
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            //upload ke api service
            val service = ApiConfig().getApiService().uploadImage(imageMultipart, description)
            service.enqueue(object : retrofit2.Callback<FileUploadResponse> {
                override fun onResponse(
                    call: Call<FileUploadResponse>,
                    response: Response<FileUploadResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(this@MainActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Gagal instance Retrofit", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this@MainActivity, "Silakan masukkan berkas gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
        }
    }

    //method ketika tombol gallery di tekan
    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        //menentukan tipe data data intent yaitu gambar dengan itpe data apapun
        intent.type = "image/*"
        Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(intent)
    }

    //method ketika camera biasa
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        //menampung gambar hasil dari intent Kamera
        createTempFile(application).also {
            //mengambil lokasi file
            val photoURI: Uri = FileProvider.getUriForFile(
                this@MainActivity,
                "com.dicoding.picodiploma.mycamera",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    //method ketika tombol camera x di tekan
    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    //variabel global menyimpan hasil foto
    private var getFile: File? = null

    //mengambil hasil capture dari cameraX menggunakan intent
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == CAMERA_X_RESULT){
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            //mengambil hasil gambar dan disimpan ke variabel yang telah dibuat
            getFile = myFile

            //hasil gambar di rotate supaya tidak miring
            val result = rotateBitmap(BitmapFactory.decodeFile(myFile.path), isBackCamera)
            binding.previewImageView.setImageBitmap(result)
        }
    }

    //mengambil hasil dari camera biasa
    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == RESULT_OK){
            //memanggil path file untuk menyimpan hasil capture kamera
            val myFile = File(currentPhotoPath)
            val imageBitmap = BitmapFactory.decodeFile(myFile.path)
            binding.previewImageView.setImageBitmap(imageBitmap)

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
            val myFile = uriToFile(selectedImg, this@MainActivity)
            binding.previewImageView.setImageURI(selectedImg)

            getFile = myFile
        }
    }
}