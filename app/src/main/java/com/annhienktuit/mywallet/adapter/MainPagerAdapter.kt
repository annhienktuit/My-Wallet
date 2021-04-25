package com.annhienktuit.mywallet.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.annhienktuit.mywallet.fragments.HomeFragment
import com.annhienktuit.mywallet.fragments.PlanningFragment
import com.annhienktuit.mywallet.fragments.ReportFragment
import com.annhienktuit.mywallet.fragments.UserFragment


//This class manages 2 fragments previous month and current month in report fragment
class MainPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior){
    override fun getCount(): Int {
        return 4 //we has 4 fragments
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> HomeFragment()
            1 -> ReportFragment()
            2 -> PlanningFragment()
            3 -> UserFragment()
            else -> HomeFragment()
        }
    }

}