package com.dicoding.picodiploma.mycamera

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.dicoding.picodiploma.mycamera.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.captureImage.setOnClickListener { takePhoto() }
        //action tombol switch camera
        binding.switchCamera.setOnClickListener {
            cameraSelector = if (cameraSelector.equals(CameraSelector.DEFAULT_BACK_CAMERA)) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    //method mengambil foto
    private fun takePhoto() {
       // takePhoto
        //memastikan imageCapture tidak dalam kondisi null
        val imageCapture = imageCapture ?: return

        /*
        * create file berguna untuk menypna gambar hasil camera ke sebuah file*/
        val photoFile = createFile(application)

        //file untuk output
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        //menggunakan method takePicture untuk mengambil gambar
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                //method ketika caoture gambar error
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@CameraActivity, "Gagal Mengambil Gambar.", Toast.LENGTH_SHORT).show()
                }

                //method ketika capture gambar berhasil
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    Toast.makeText(this@CameraActivity, "Berhasil Mengambil Gambar.", Toast.LENGTH_SHORT).show()
                    //mengirim gambar ke mainActivity
                    val i = Intent()
                    i.putExtra("picture", photoFile)
                    i.putExtra("isBackCamera", cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    setResult(MainActivity.CAMERA_X_RESULT, i)
                    finish()
                }
            }
        )
    }

    private var imageCapture: ImageCapture? = null
    //camera selector untuk menentukan kamera mana yang digunakan pertama kali
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    //method menampilkan cameraX
    private fun startCamera() {
        // showCamera
        //instance untuk mengikat camera ke activity
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            //mengikat kamera ke lifeCyvleOwner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(binding.viewFinder.surfaceProvider) }

            //menetapkan imageCapture untuk menggunakan method TakePhoto
            imageCapture = ImageCapture.Builder().build()

            //try cacth untuk menghubungkan ketiga hal yang telah di inisialiasikan di atas
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            }catch (exc: java.lang.Exception){
                Toast.makeText(this, "Gagal Memunculkan kamera.", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}