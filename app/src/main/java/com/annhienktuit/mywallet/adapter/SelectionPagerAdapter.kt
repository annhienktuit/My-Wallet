package com.annhienktuit.mywallet.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SelectionPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
    private var fragmentList: MutableList<Fragment> = mutableListOf()
    private var titleList: MutableList<String> = mutableListOf()

    // get fragment from fragmentList
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    //Count the amount of fragments
    override fun getCount(): Int {
        return fragmentList.count()
    }

    override fun getPageTitle(position: Int) : String{
        return titleList[position]
    }

    fun addFragment(fragment: Fragment, title: String){
        fragmentList.add(fragment)
        titleList.add(title)
    }
}