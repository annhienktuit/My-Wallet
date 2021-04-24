package com.annhienktuit.mywallet.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.annhienktuit.mywallet.fragments.currentMonthFragment
import com.annhienktuit.mywallet.fragments.previousMonthFragment

//This class manages 2 fragments previous month and current month in report fragment
class SelectionPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior){
    override fun getCount(): Int {
        return 2 //we has only two child fragments in report fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> return "Previous Month"
            1 -> return "Current Month"
        }
        return super.getPageTitle(position)
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> previousMonthFragment()
            1 -> currentMonthFragment()
            else -> previousMonthFragment()
        }
    }

}