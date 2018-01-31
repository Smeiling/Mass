package com.sml.mass

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.sml.mass.adapter.HomePagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var fragmentList = mutableListOf("com.sml.mass.fragment.ComponentsFragment",
                "com.sml.mass.fragment.ToolsFragment")
        val pagerAdapter = HomePagerAdapter(supportFragmentManager, fragmentList)
        view_pager.adapter = pagerAdapter
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                updateBottomBar(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        ll_component.setOnClickListener { view_pager?.setCurrentItem(0, true) }
        ll_tools.setOnClickListener { view_pager?.setCurrentItem(1, true) }

    }

    fun updateBottomBar(index: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (index == 0) {
                icon_components.imageTintList = ColorStateList.valueOf(Color.parseColor("#333333"))
                icon_tools.imageTintList = ColorStateList.valueOf(Color.parseColor("#999999"))
                label_tools.setTextColor(Color.parseColor("#999999"))
                label_components.setTextColor(Color.parseColor("#333333"))
            } else {
                icon_components.imageTintList = ColorStateList.valueOf(Color.parseColor("#999999"))
                label_components.setTextColor(Color.parseColor("#999999"))
                icon_tools.imageTintList = ColorStateList.valueOf(Color.parseColor("#333333"))
                label_tools.setTextColor(Color.parseColor("#333333"))

            }
        }
    }
}
