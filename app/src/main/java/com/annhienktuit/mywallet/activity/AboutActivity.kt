package com.annhienktuit.mywallet.activity

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.`object`.CoderModel
import com.annhienktuit.mywallet.adapter.CoderAdapter
import kotlinx.android.synthetic.main.activity_about.*

private var mContext: Context? = null
class AboutActivity : AppCompatActivity() {
    private lateinit var coderModelList: ArrayList<CoderModel>
    private lateinit var coderAdapter: CoderAdapter
    val actionBar = this.supportActionBar!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        setContentView(R.layout.activity_about)
        loadData()
        viewpager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                actionBar.title = "About Us"
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
//        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun loadData() {
        coderModelList = ArrayList()
        coderModelList.add(
            CoderModel("Nhien Nguyen",
            "SE faculty\n Intern at Cohota",
            R.drawable.avt_nhien_deptrai))
        coderModelList.add(CoderModel("Khai Hoan",
            "SE faculty\n Published 2 apps on Play Store",
            R.drawable.avt_nhien_deptrai))
        coderModelList.add(CoderModel("Hien Tran",
            "SE faculty\n Academic scholarship at 2nd semester",
            R.drawable.avt_nhien_deptrai))
        coderAdapter = CoderAdapter(applicationContext , coderModelList)
        viewpager.adapter = coderAdapter
        viewpager.setPadding(100,0,100,0)
    }
}