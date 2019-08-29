package demo.dev.flexmedia.co.th.mlkit_flex_demo.TextRecognition

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar

import com.google.firebase.ml.vision.common.FirebaseVisionImage
import demo.dev.flexmedia.co.th.mlkit_flex_demo.Camera.ImageConstant.REQUEST_CODE_SELECT_FILE
import demo.dev.flexmedia.co.th.mlkit_flex_demo.Camera.ImageConstant.REQUEST_CODE_TAKE_PHOTO
import demo.dev.flexmedia.co.th.mlkit_flex_demo.Camera.ImageProvider
import demo.dev.flexmedia.co.th.mlkit_flex_demo.Camera.ImageProvider.mCurrentPhotoPath
import demo.dev.flexmedia.co.th.mlkit_flex_demo.Camera.ImageProvider.outputUri
import demo.dev.flexmedia.co.th.mlkit_flex_demo.R
import demo.dev.flexmedia.co.th.mlkit_flex_demo.permission.CameraPermission
import demo.dev.flexmedia.co.th.mlkit_flex_demo.permission.CameraPermission.REQUEST_STORAGE
import kotlinx.android.synthetic.main.activity_text_recognition.*
import java.io.File
import java.io.IOException


class TextRecognitionActivity : AppCompatActivity() {

    private val TAG = TextRecognitionActivity::class.java!!.getSimpleName()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_recognition)

        val back: ImageView = findViewById(R.id.imageView2)
        back.setOnClickListener {
            finish()
        }


        val constraintLayout2:ConstraintLayout = findViewById(R.id.constraintLayout2)
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
        ImageProvider.dispatchTakePictureIntent(this@TextRecognitionActivity, this@TextRecognitionActivity)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_STORAGE -> {
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
            val finalUri = Uri.fromFile(File(mCurrentPhotoPath))
            ImageProvider.setPhotoSelect(this, finalUri)
        } else if (resultCode == Activity.RESULT_OK && !mCurrentPhotoPath.equals("")) {
            try {
                handleCameraPhoto(requestCode, data)
            } catch (e: Exception) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
        }
    }

    private fun handleCameraPhoto(request: Int, data: Intent?) {
        try {
            if (mCurrentPhotoPath != null) {
                if (request == REQUEST_CODE_TAKE_PHOTO) {
                    outputUri = ImageProvider.galleryAddPic(this)
                } else if (request == REQUEST_CODE_SELECT_FILE) {
                    outputUri = data?.data
                }
                val intent = Intent(this@TextRecognitionActivity,TextRecognitionResultActivity::class.java)
                intent.putExtra("URI",outputUri.toString())
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
        }

    }
}
