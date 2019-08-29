package demo.dev.flexmedia.co.th.mlkit_flex_demo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import demo.dev.flexmedia.co.th.mlkit_flex_demo.CONSTANT.Companion.FACE_DETECTION
import demo.dev.flexmedia.co.th.mlkit_flex_demo.CONSTANT.Companion.TEXT_RECOGNITION
import demo.dev.flexmedia.co.th.mlkit_flex_demo.FaceDetection.FaceDetectionActivity
import demo.dev.flexmedia.co.th.mlkit_flex_demo.TextRecognition.TextRecognitionActivity
import kotlinx.android.synthetic.main.fragment_page.*


class PageFragment : Fragment() {
    companion object{
        private val KEY = "key"
        private val HEADER = "header"

        @JvmStatic fun newInstance(key:String,header:String) =
            PageFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY, key)
                    putString(HEADER, header)
                }
            }
    }

    var text  = ""
    var key  = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            text = it.getString(HEADER)
            key = it.getString(KEY)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val pageView = inflater.inflate(R.layout.fragment_page, container, false)

        val textView: TextView = pageView.findViewById(R.id.header) as TextView
        val button:Button = pageView.findViewById(R.id.button) as Button
        textView.text = text
        onClickInitial(button)
        return pageView
    }

    fun onClickInitial(button: Button){
        button.setOnClickListener {
            if (key.equals(TEXT_RECOGNITION,ignoreCase = true)){
                startActivity(Intent(activity,TextRecognitionActivity::class.java))
            }else if(key.equals(FACE_DETECTION,ignoreCase = true)){
                startActivity(Intent(activity,FaceDetectionActivity::class.java))
            }
        }
    }


}
