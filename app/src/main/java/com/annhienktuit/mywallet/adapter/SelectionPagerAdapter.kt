package com.annhienktuit.mywallet.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.annhienktuit.mywallet.fragments.currentMonthFragment
import com.annhienktuit.mywallet.fragments.previousMonthFragment

//This class manages 2 fragments previous month and current month in report fragment
class SelectionPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm){
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
        when(position){
            0 -> return previousMonthFragment()
            1 -> return currentMonthFragment()
            else -> return previousMonthFragment()
        }
    }

}