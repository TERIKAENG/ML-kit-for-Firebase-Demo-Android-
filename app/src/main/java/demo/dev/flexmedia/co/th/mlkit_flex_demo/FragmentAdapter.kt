package demo.dev.flexmedia.co.th.mlkit_flex_demo

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.ArrayList

class FragmentAdapter(private val fragmentManager: FragmentManager, data: MutableList<Fragment> ) :
    FragmentPagerAdapter(fragmentManager) {
    private var data: MutableList<Fragment> = mutableListOf<Fragment>()

    init {
        this.data = data
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Fragment {
        return data[position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        if (!data.contains(`object`)) {
            fragmentManager.beginTransaction().remove(`object` as Fragment).commit()
        }
    }
}