package com.annhienktuit.mywallet.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.annhienktuit.mywallet.R
import com.annhienktuit.mywallet.activity.AllMonthReport
import com.annhienktuit.mywallet.activity.MainActivity
import com.annhienktuit.mywallet.adapter.ReportPagerAdapter
import com.annhienktuit.mywallet.utils.Extensions
import com.google.android.material.tabs.TabLayout

class ReportFragment : Fragment() {

    private lateinit var myView: View
    private lateinit var myPager: ViewPager
    private lateinit var myTab: TabLayout
    private lateinit var seeMoreBtn: Button

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_report, container, false)
        setData(myView)

        seeMoreBtn.setOnClickListener {
            val intent = Intent(activity, AllMonthReport::class.java)
            activity?.startActivity(intent)
        }

        myPager = myView.findViewById(R.id.viewPager)
        myTab = myView.findViewById(R.id.tabLayout)
        var adapter = ReportPagerAdapter(childFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        myPager.adapter = adapter

        myTab.setupWithViewPager(myPager)
        myPager.currentItem = 1
        return myView
    }
    fun setData(view: View){
        seeMoreBtn = view.findViewById(R.id.seeMoreBtn)

        var data = (activity as MainActivity)
        var name = data.getName()
        var balance = data.getBalance()
        var income = data.getIncome()
        var expense = data.getExpense()
        var txtMoney = view.findViewById<TextView>(R.id.txtMoney)
        txtMoney.text = Extensions.changeToMoney(balance) + " VND"
    }

}

