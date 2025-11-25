package com.example.lab_week_11_b

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 3
    }

    private lateinit var providerFileManager: ProviderFileManager

    private var photoInfo: FileInfo? = null
    private var videoInfo: FileInfo? = null

    private var isCapturingVideo = false

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var takeVideoLauncher: ActivityResultLauncher<Uri>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        providerFileManager = ProviderFileManager(
            applicationContext,
            FileHelper(applicationContext),
            contentResolver,
            Executors.newSingleThreadExecutor(),
            MediaContentHelper()
        )

        // Camera launchers
        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) {
                providerFileManager.insertImageToStore(photoInfo)
            }

        takeVideoLauncher =
            registerForActivityResult(ActivityResultContracts.CaptureVideo()) {
                providerFileManager.insertVideoToStore(videoInfo)
            }

        // Buttons
        findViewById<Button>(R.id.photo_button).setOnClickListener {
            isCapturingVideo = false
            checkStoragePermission { openImageCapture() }
        }

        findViewById<Button>(R.id.video_button).setOnClickListener {
            isCapturingVideo = true
            checkStoragePermission { openVideoCapture() }
        }
    }

    /**
     * Check WRITE_EXTERNAL_STORAGE for Android 9 and below.
     * Android 10+ does not require it.
     */
    private fun checkStoragePermission(onGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ --> no permission needed
            onGranted()
            return
        }

        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE

        // Android 9 and below
        if (ContextCompat.checkSelfPermission(this, permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            onGranted()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    /**
     * Handle permission result for Android 9 and below.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != REQUEST_EXTERNAL_STORAGE) return

        if (grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // Open the appropriate camera action
            if (isCapturingVideo) openVideoCapture()
            else openImageCapture()
        }
    }

    /**
     * Open camera to capture an image.
     */
    private fun openImageCapture() {
        val time = System.currentTimeMillis()
        photoInfo = providerFileManager.generatePhotoUri(time)
        takePictureLauncher.launch(photoInfo!!.uri)
    }

    /**
     * Open camera to capture a video.
     */
    private fun openVideoCapture() {
        val time = System.currentTimeMillis()
        videoInfo = providerFileManager.generateVideoUri(time)
        takeVideoLauncher.launch(videoInfo!!.uri)
    }
}
