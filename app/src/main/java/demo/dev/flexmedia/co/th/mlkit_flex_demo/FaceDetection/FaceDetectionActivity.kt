package demo.dev.flexmedia.co.th.mlkit_flex_demo.FaceDetection

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import demo.dev.flexmedia.co.th.mlkit_flex_demo.Camera.ImageConstant
import demo.dev.flexmedia.co.th.mlkit_flex_demo.Camera.ImageProvider
import demo.dev.flexmedia.co.th.mlkit_flex_demo.R
import demo.dev.flexmedia.co.th.mlkit_flex_demo.TextRecognition.TextRecognitionActivity
import demo.dev.flexmedia.co.th.mlkit_flex_demo.TextRecognition.TextRecognitionResultActivity
import demo.dev.flexmedia.co.th.mlkit_flex_demo.permission.CameraPermission
import java.io.File

class FaceDetectionActivity : AppCompatActivity() {


    private val TAG = TextRecognitionActivity::class.java!!.getSimpleName()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_detection)

        val back:ImageView = findViewById(R.id.imageView2)
        back.setOnClickListener {
            finish()
        }

        val constraintLayout2: ConstraintLayout = findViewById(R.id.constraintLayout2)
        constraintLayout2.setOnClickListener {
            cameraPermission()
        }
    }


        private fun cameraPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!CameraPermission.verify(this)) {
                    startCamera()
                }
            } else {
                startCamera()
            }
        }

        private fun startCamera() {
            ImageProvider.dispatchTakePictureIntent(this@FaceDetectionActivity, this@FaceDetectionActivity)
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            when (requestCode) {
                CameraPermission.REQUEST_STORAGE -> {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startCamera()
                    } else {
                        Snackbar.make(window.decorView.rootView, "Permission denied", Snackbar.LENGTH_SHORT).show()
                    }
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
                else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == Activity.RESULT_OK && requestCode == 91) {
                val finalUri = Uri.fromFile(File(ImageProvider.mCurrentPhotoPath))
                ImageProvider.setPhotoSelect(this, finalUri)
            } else if (resultCode == Activity.RESULT_OK && !ImageProvider.mCurrentPhotoPath.equals("")) {
                try {
                    handleCameraPhoto(requestCode, data)
                } catch (e: Exception) {
                    Log.e(TAG, Log.getStackTraceString(e))
                }
            }
        }

        private fun handleCameraPhoto(request: Int, data: Intent?) {
            try {
                if (ImageProvider.mCurrentPhotoPath != null) {
                    if (request == ImageConstant.REQUEST_CODE_TAKE_PHOTO) {
                        ImageProvider.outputUri = ImageProvider.galleryAddPic(this)
                    } else if (request == ImageConstant.REQUEST_CODE_SELECT_FILE) {
                        ImageProvider.outputUri = data?.data
                    }
                    val intent = Intent(this@FaceDetectionActivity, FaceDetectionResultActivity::class.java)
                    intent.putExtra("URI", ImageProvider.outputUri.toString())
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Log.e(TAG, Log.getStackTraceString(e))
            }

        }

}

