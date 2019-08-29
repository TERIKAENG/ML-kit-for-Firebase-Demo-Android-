package demo.dev.flexmedia.co.th.mlkit_flex_demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import demo.dev.flexmedia.co.th.mlkit_flex_demo.CONSTANT.Companion.FACE_DETECTION
import demo.dev.flexmedia.co.th.mlkit_flex_demo.CONSTANT.Companion.TEXT_RECOGNITION
import demo.dev.flexmedia.co.th.mlkit_flex_demo.PageFragment.Companion.newInstance
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialUI();
    }

    fun initialUI(){
        val adapter = FragmentAdapter(supportFragmentManager, providesAllFragment())
        myviewpager.setAdapter(adapter)
        recyclerview_pager_indicator.attachToViewPager(myviewpager);
        myviewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

    fun providesAllFragment():MutableList<Fragment> {
        var myList: MutableList<Fragment> = mutableListOf<Fragment>()
        myList.add(newInstance(TEXT_RECOGNITION,getString(R.string.header_text_recognition)))
        myList.add(newInstance(FACE_DETECTION,getString(R.string.header_face_detection)))
        return myList
    }
}
