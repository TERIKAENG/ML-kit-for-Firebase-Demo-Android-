package demo.dev.flexmedia.co.th.mlkit_flex_demo.TextRecognition

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import java.io.IOException
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionText
import demo.dev.flexmedia.co.th.mlkit_flex_demo.R
import java.util.*
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer


class TextRecognitionResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_recognition_result)

        val uri: String = intent.getStringExtra("URI")
        val myUri = Uri.parse(uri)

        val tv: TextView = findViewById(R.id.textView);
        val iv: ImageView = findViewById(R.id.imageView3)
        val onDevice:Button = findViewById(R.id.button2)
        val onCould:Button = findViewById(R.id.button3)
        val back:ImageView = findViewById(R.id.imageView2)
        back.setOnClickListener {
            finish()
        }


        iv.setImageURI(myUri)

        //สร้าง FirebaseVisionImage
        //สร้างจาก File: วิธีนี้ให้ส่งค่า context และ URI ของไฟล์เข้าไปสร้าง
        val image: FirebaseVisionImage
        try {
            image = FirebaseVisionImage.fromFilePath(this@TextRecognitionResultActivity, myUri)
            onDevice.setOnClickListener {
                recognizeText(image,tv)
            }
            onCould.setOnClickListener {
                recognizeTextCloud(image,tv)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    // on-device model
    private fun recognizeText(image: FirebaseVisionImage,textView: TextView) {

        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        val result = detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                for (block in firebaseVisionText.textBlocks) {
                    val text = block.text
                    textView.text  = text
                }
            }
            .addOnFailureListener {
                Toast.makeText(this,"ERROR",Toast.LENGTH_LONG).show()
            }
    }



    // could model
    private fun recognizeTextCloud(image: FirebaseVisionImage,textView: TextView) {

        val options = FirebaseVisionCloudTextRecognizerOptions.Builder()
            .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
            .build()

        val detector = FirebaseVision.getInstance().getCloudTextRecognizer(options)
        val result = detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                for (block in firebaseVisionText.textBlocks) {
                    val text = block.text
                    textView.text  = text
                }
            }
            .addOnFailureListener {
                Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                print(it.message)
            }
    }

}
