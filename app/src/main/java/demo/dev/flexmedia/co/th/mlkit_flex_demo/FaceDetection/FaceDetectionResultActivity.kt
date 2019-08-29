package demo.dev.flexmedia.co.th.mlkit_flex_demo.FaceDetection

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import demo.dev.flexmedia.co.th.mlkit_flex_demo.R
import java.io.IOException

class FaceDetectionResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_detection_result)

        val uri: String = intent.getStringExtra("URI")
        val myUri = Uri.parse(uri)

        val tv: TextView = findViewById(R.id.textView);
        val iv: ImageView = findViewById(R.id.imageView3)
        val onDevice: Button = findViewById(R.id.button2)
        val back:ImageView = findViewById(R.id.imageView2)
        back.setOnClickListener {
            finish()
        }

        iv.setImageURI(myUri)

        //สร้าง FirebaseVisionImage
        //สร้างจาก File: วิธีนี้ให้ส่งค่า context และ URI ของไฟล์เข้าไปสร้าง
        val image: FirebaseVisionImage
        try {
            image = FirebaseVisionImage.fromFilePath(this@FaceDetectionResultActivity, myUri)
            onDevice.setOnClickListener {
                detectFaces(image,tv)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun detectFaces(image: FirebaseVisionImage,textView: TextView) {

        var txtResult = ""

        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()

        val detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(options)

        val result = detector.detectInImage(image)
            .addOnSuccessListener { faces ->
                for (face in faces) {
                    val leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)
                    leftEar?.let {
                        val leftEarPos = leftEar.position
                    }

                    // If classification was enabled:
                    if (face.smilingProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                        val smileProb = face.smilingProbability
                        if (smileProb>0.8){
                            txtResult = txtResult + "SMILE : YES\n"
                        }else{
                            txtResult = txtResult + "SMILE : NO\n"
                        }
                    }
                    if (face.rightEyeOpenProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                        val rightEyeOpenProb = face.rightEyeOpenProbability
                        if (rightEyeOpenProb>0.8){
                            txtResult = txtResult + "Right eye open : YES\n"
                        }else{
                            txtResult = txtResult + "Right eye open : NO\n"
                        }
                    }

                    if (face.leftEyeOpenProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                        val leftEyeOpenProb = face.leftEyeOpenProbability
                        if (leftEyeOpenProb>0.8){
                            txtResult = txtResult + "Left eye open : YES\n"
                        }else{
                            txtResult = txtResult + "Left eye open : NO\n"
                        }
                    }

                    textView.text = txtResult

                    // If face tracking was enabled:
                    if (face.trackingId != FirebaseVisionFace.INVALID_ID) {
                        val id = face.trackingId
                    }
                }
            }
            .addOnFailureListener(
                object : OnFailureListener {
                    override fun onFailure(e: Exception) {
                        Toast.makeText(this@FaceDetectionResultActivity,e.message, Toast.LENGTH_LONG).show()
                        print(e.message)
                    }
                })
    }

}
